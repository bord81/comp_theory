sealed class SimType {
    fun isReducible(): Boolean {
        return when (this) {
            is NumberSim -> false
            else -> true
        }
    }

    open fun reduce(env: Map<String, SimType>): SimType {
        return this
    }

    open fun value(): Int {
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
}

class AddSim(left: SimType, right: SimType) : SimType() {
    private val left = left
    private val right = right
    override fun toString(): String {
        return "$left + $right"
    }

    override fun reduce(env: Map<String, SimType>): SimType {
        return when {
            left.isReducible() -> AddSim(left.reduce(env), right)
            right.isReducible() -> AddSim(left, right.reduce(env))
            else -> NumberSim(left.value() + right.value())
        }
    }
}

class MultiplySim(left: SimType, right: SimType) : SimType() {
    private val left = left
    private val right = right
    override fun toString(): String {
        return "$left * $right"
    }

    override fun reduce(env: Map<String, SimType>): SimType {
        return when {
            left.isReducible() -> MultiplySim(left.reduce(env), right)
            right.isReducible() -> MultiplySim(left, right.reduce(env))
            else -> NumberSim(left.value() * right.value())
        }
    }
}

class Variable(name: String) :SimType() {
    private val name = name
    override fun toString(): String {
        return "$name"
    }

    override fun reduce(env: Map<String, SimType>): SimType {
        return env[name] ?: error("$name is not available in environment")
    }
}

class Machine(expr: SimType, env: Map<String, SimType>) {
    private var expr = expr
    private val env = env
    private fun step() {
        expr = expr.reduce(env)
    }

    fun run() {
        while (expr.isReducible()) {
            println(expr)
            step()
        }
        println(expr)
    }
}

fun main() {
    val testExpr = AddSim(Variable("x"), Variable("y"))
    val environment = mapOf("x" to NumberSim(3), "y" to NumberSim(4))
    val vm = Machine(testExpr, environment)
    vm.run()
}