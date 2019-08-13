sealed class SimType {
    fun isReducible(): Boolean {
        return when (this) {
            is NumberSim -> false
            is BoolSim -> false
            is DoNothing -> false
            else -> true
        }
    }

    open fun reduce(env: Map<String, SimType>): Pair<SimType, Map<String, SimType>> {
        return Pair(this, mapOf())
    }

    open fun value(): Int {
        throw UnsupportedOperationException()
    }

    open fun boolValue(): Boolean {
        throw UnsupportedOperationException()
    }
}

class NumberSim(value: Int) : SimType() {
    private val value = value
    override fun toString(): String {
        return "$value"
    }

    override fun value(): Int {
        return value
    }

    override fun boolValue(): Boolean {
        return when (value) {
            0 -> false
            else -> true
        }
    }
}

class BoolSim(value: Boolean) : SimType() {
    private val value = value
    override fun toString(): String {
        return "$value"
    }

    override fun value(): Int {
        return when (value) {
            false -> 0
            true -> 1
        }
    }

    override fun boolValue(): Boolean {
        return value
    }
}

class AddSim(left: SimType, right: SimType) : SimType() {
    private val left = left
    private val right = right
    override fun toString(): String {
        return "$left + $right"
    }

    override fun reduce(env: Map<String, SimType>): Pair<SimType, Map<String, SimType>> {
        return when {
            left.isReducible() -> Pair(AddSim(left.reduce(env).first, right), env)
            right.isReducible() -> Pair(AddSim(left, right.reduce(env).first), env)
            else -> Pair(NumberSim(left.value() + right.value()), env)
        }
    }
}

class MultiplySim(left: SimType, right: SimType) : SimType() {
    private val left = left
    private val right = right
    override fun toString(): String {
        return "$left * $right"
    }

    override fun reduce(env: Map<String, SimType>): Pair<SimType, Map<String, SimType>> {
        return when {
            left.isReducible() -> Pair(MultiplySim(left.reduce(env).first, right), env)
            right.isReducible() -> Pair(MultiplySim(left, right.reduce(env).first), env)
            else -> Pair(NumberSim(left.value() * right.value()), env)
        }
    }
}

class LessSim(left: SimType, right: SimType) : SimType() {
    private val left = left
    private val right = right
    override fun toString(): String {
        return "$left < $right"
    }

    override fun reduce(env: Map<String, SimType>): Pair<SimType, Map<String, SimType>> {
        return when {
            left.isReducible() -> Pair(LessSim(left.reduce(env).first, right), env)
            right.isReducible() -> Pair(LessSim(left, right.reduce(env).first), env)
            else -> Pair(BoolSim(left.value() < right.value()), env)
        }
    }
}

class Variable(name: String) : SimType() {
    private val name = name
    override fun toString(): String {
        return "$name"
    }

    override fun reduce(env: Map<String, SimType>): Pair<SimType, Map<String, SimType>> {
        if (env.containsKey(name)) {
            return Pair(env[name], env) as Pair<SimType, Map<String, SimType>>
        } else error("$name is not available in environment")
    }
}

class DoNothing : SimType() {
    override fun toString(): String {
        return "do-nothing"
    }

    override fun equals(other: Any?): Boolean {
        return other is DoNothing
    }
}

class Assign(name: String, expr: SimType) : SimType() {
    private val name = name
    private val expr = expr
    override fun toString(): String {
        return "$name = $expr"
    }

    override fun reduce(env: Map<String, SimType>): Pair<SimType, Map<String, SimType>> {
        return if (expr.isReducible()) {
            Pair(Assign(name, expr.reduce(env).first), env)
        } else {
            val temp = env.toMutableMap()
            temp[name] = expr
            Pair(DoNothing(), temp.toMap())
        }
    }
}

class If(condition: SimType, consequence: SimType, alternative: SimType) : SimType() {
    private val cond = condition
    private val conseq = consequence
    private val alt = alternative
    override fun toString(): String {
        return "if $cond then $conseq else $alt"
    }

    override fun reduce(env: Map<String, SimType>): Pair<SimType, Map<String, SimType>> {
        return if (cond.isReducible()) {
            Pair(If(cond.reduce(env).first, conseq, alt), env)
        } else {
            when {
                cond.boolValue() -> Pair(conseq, env)
                else -> Pair(alt, env)
            }
        }
    }
}

class Machine(expr: SimType, env: Map<String, SimType>) {
    private var expr = expr
    private var env = env
    private fun step() {
        val eval = expr.reduce(env)
        expr = eval.first
        env = eval.second
    }

    fun run() {
        while (expr.isReducible()) {
            println(expr)
            println(env)
            step()
        }
        println(expr)
        println(env)
    }
}

fun main() {
    val testExpr = AddSim(Variable("x"), Variable("y"))
    val environment = mutableMapOf("x" to NumberSim(3), "y" to NumberSim(4))
    val vm = Machine(testExpr, environment)
    vm.run()

    println()
    val testExpr2 = Assign("x", AddSim(Variable("x"), NumberSim(1)))
    val environment2 = mutableMapOf("x" to NumberSim(2))
    val vm2 = Machine(testExpr2, environment2)
    vm2.run()

    println()
    val testExpr3 = If(Variable("x"),
        Assign("y", NumberSim(1)), Assign("y", NumberSim(2)))
    val environment3 = mutableMapOf("x" to NumberSim(0))
    val vm3 = Machine(testExpr3, environment3)
    vm3.run()
}