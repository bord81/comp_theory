package comp_theory

/* Simple language using big step semantics */
private sealed class SimBigType {
    open fun evaluate(env: Map<String, SimBigType>): Pair<SimBigType, Map<String, SimBigType>> {
        return Pair(this, mapOf())
    }

    open fun value(): Int {
        throw UnsupportedOperationException()
    }

    open fun boolValue(): Boolean {
        throw UnsupportedOperationException()
    }
}

private class NumberBigSim(value: Int) : SimBigType() {
    private val value = value
    override fun toString(): String {
        return "$value"
    }

    override fun value(): Int {
        return value
    }

    override fun boolValue(): Boolean {
        return value != 0
    }

    override fun evaluate(env: Map<String, SimBigType>): Pair<SimBigType, Map<String, SimBigType>> {
        return Pair(this, env)
    }
}

private class BoolBigSim(value: Boolean) : SimBigType() {
    private val value = value
    override fun toString(): String {
        return "$value"
    }

    override fun value(): Int {
        return if (value) 1 else 0
    }

    override fun boolValue(): Boolean {
        return value
    }

    override fun evaluate(env: Map<String, SimBigType>): Pair<SimBigType, Map<String, SimBigType>> {
        return Pair(this, env)
    }
}

private class VarBigSim(name: String) : SimBigType() {
    private val name = name
    override fun toString(): String {
        return "$name"
    }

    override fun evaluate(env: Map<String, SimBigType>): Pair<SimBigType, Map<String, SimBigType>> {
        return Pair(env[name]!!, env)
    }
}

private class AddBigSim(left: SimBigType, right: SimBigType) : SimBigType() {
    private val left = left
    private val right = right
    override fun toString(): String {
        return "$left + $right"
    }

    override fun evaluate(env: Map<String, SimBigType>): Pair<SimBigType, Map<String, SimBigType>> {
        return Pair(NumberBigSim(left.evaluate(env).first.value() + right.evaluate(env).first.value()), env)
    }
}

private class MultBigSim(left: SimBigType, right: SimBigType) : SimBigType() {
    private val left = left
    private val right = right
    override fun toString(): String {
        return "$left * $right"
    }

    override fun evaluate(env: Map<String, SimBigType>): Pair<SimBigType, Map<String, SimBigType>> {
        return Pair(NumberBigSim(left.evaluate(env).first.value() * right.evaluate(env).first.value()), env)
    }
}

private class LessBigSim(left: SimBigType, right: SimBigType) : SimBigType() {
    private val left = left
    private val right = right
    override fun toString(): String {
        return "$left < $right"
    }

    override fun evaluate(env: Map<String, SimBigType>): Pair<SimBigType, Map<String, SimBigType>> {
        return Pair(BoolBigSim(left.evaluate(env).first.value() < right.evaluate(env).first.value()), env)
    }
}

private class AssignBigSim(name: String, expr: SimBigType) : SimBigType() {
    private val name = name
    private val expr = expr
    override fun toString(): String {
        return "$name = $expr"
    }

    override fun evaluate(env: Map<String, SimBigType>): Pair<SimBigType, Map<String, SimBigType>> {
        val temp = env.toMutableMap()
        temp[name] = expr.evaluate(env).first
        return Pair(DoNothingBigSim(), temp)
    }

}

private class DoNothingBigSim : SimBigType() {
    override fun toString(): String {
        return "do-nothing"
    }

    override fun evaluate(env: Map<String, SimBigType>): Pair<SimBigType, Map<String, SimBigType>> {
        return Pair(this, env)
    }
}

private class IfBigSim(condition: SimBigType, consequence: SimBigType, alternative: SimBigType) : SimBigType() {
    private val cond = condition
    private val conseq = consequence
    private val alt = alternative
    override fun toString(): String {
        return "if $cond then $conseq else $alt"
    }

    override fun evaluate(env: Map<String, SimBigType>): Pair<SimBigType, Map<String, SimBigType>> {
        return if (cond.evaluate(env).first.boolValue()) {
            conseq.evaluate(env)
        } else {
            alt.evaluate(env)
        }
    }
}

private class SequenceBigSim(first: SimBigType, second: SimBigType) : SimBigType() {
    private val first = first
    private val second = second
    override fun toString(): String {
        return "$first; $second;"
    }

    override fun evaluate(env: Map<String, SimBigType>): Pair<SimBigType, Map<String, SimBigType>> {
        return second.evaluate(first.evaluate(env).second)
    }
}

private class WhileBigSim(condition: SimBigType, body: SimBigType) : SimBigType() {
    private val cond = condition
    private val body = body
    override fun toString(): String {
        return "while($cond) {$body}"
    }

    override fun evaluate(env: Map<String, SimBigType>): Pair<SimBigType, Map<String, SimBigType>> {
        var result_env: MutableMap<String, SimBigType>
        if (cond.evaluate(env).first.boolValue()) {
            result_env = evaluate(body.evaluate(env).second).second.toMutableMap()
        } else {
            result_env = env.toMutableMap()
        }
        return Pair(this, result_env)
    }
}

fun main() {
    println(NumberBigSim(5).evaluate(mutableMapOf()))

    println(VarBigSim("x").evaluate(mutableMapOf(Pair("x", NumberBigSim(20)))))

    println(AddBigSim(NumberBigSim(5), NumberBigSim(8)).evaluate(mutableMapOf()))

    println(
        LessBigSim(AddBigSim(VarBigSim("x"), NumberBigSim(2)), VarBigSim("y"))
            .evaluate(mutableMapOf(Pair("x", NumberBigSim(2)), Pair("y", NumberBigSim(5))))
    )

    val statement1 = SequenceBigSim(
        AssignBigSim("x", AddBigSim(NumberBigSim(1), NumberBigSim(1))),
        AssignBigSim("y", AddBigSim(VarBigSim("x"), NumberBigSim(3)))
    )
    println(statement1)
    println(statement1.evaluate(mutableMapOf()))

    val statement2 = WhileBigSim(
        LessBigSim(VarBigSim("x"), NumberBigSim(5)),
        AssignBigSim("x", MultBigSim(VarBigSim("x"), NumberBigSim(3)))
    )
    println(statement2)
    println(statement2.evaluate(mutableMapOf(Pair("x", NumberBigSim(1)))))
}