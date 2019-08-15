package comp_theory

/* Simple language using big step semantics */
private sealed class SimBigType {
    open fun evaluate(env: Map<String, SimBigType>): SimBigType {
        return this
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

    override fun evaluate(env: Map<String, SimBigType>): SimBigType {
        return this
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

    override fun evaluate(env: Map<String, SimBigType>): SimBigType {
        return this
    }
}

private class VarBigSim(name: String) : SimBigType() {
    private val name = name
    override fun toString(): String {
        return "$name"
    }

    override fun evaluate(env: Map<String, SimBigType>): SimBigType {
        return env[name]!!
    }
}

private class AddBigSim(left: SimBigType, right: SimBigType) : SimBigType() {
    private val left = left
    private val right = right
    override fun toString(): String {
        return "$left + $right"
    }

    override fun evaluate(env: Map<String, SimBigType>): SimBigType {
        return NumberBigSim(left.evaluate(env).value() + right.evaluate(env).value())
    }
}

private class MultBigSim(left: SimBigType, right: SimBigType) : SimBigType() {
    private val left = left
    private val right = right
    override fun toString(): String {
        return "$left * $right"
    }

    override fun evaluate(env: Map<String, SimBigType>): SimBigType {
        return NumberBigSim(left.evaluate(env).value() * right.evaluate(env).value())
    }
}

private class LessBigSim(left: SimBigType, right: SimBigType) : SimBigType() {
    private val left = left
    private val right = right
    override fun toString(): String {
        return "$left < $right"
    }

    override fun evaluate(env: Map<String, SimBigType>): SimBigType {
        return BoolBigSim(left.evaluate(env).value() < right.evaluate(env).value())
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
}