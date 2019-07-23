abstract class SimType {
    abstract fun isReducible(): Boolean
    abstract fun reduce(): SimType
    abstract fun value(): Int
}

class NumberSim(value: Int) : SimType() {
    private val value = value
    override fun toString(): String {
        return "$value"
    }

    override fun value(): Int {
        return value
    }

    override fun reduce(): SimType {
        return this
    }

    override fun isReducible(): Boolean {
        return false
    }
}

class AddSim(left: SimType, right: SimType) : SimType() {
    private val left = left
    private val right = right
    override fun toString(): String {
        return "$left + $right"
    }

    override fun value(): Int {
        return 0
    }

    override fun isReducible(): Boolean {
        return true
    }

    override fun reduce(): SimType {
        return when {
            left.isReducible() -> AddSim(left.reduce(), right)
            right.isReducible() -> AddSim(left, right.reduce())
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

    override fun value(): Int {
        return 0
    }

    override fun isReducible(): Boolean {
        return true
    }

    override fun reduce(): SimType {
        return when {
            left.isReducible() -> MultiplySim(left.reduce(), right)
            right.isReducible() -> MultiplySim(left, right.reduce())
            else -> NumberSim(left.value() * right.value())
        }
    }
}

fun main() {
    var addSim : SimType = AddSim(MultiplySim(NumberSim(1), NumberSim(2)),
        MultiplySim(NumberSim(3), NumberSim(4)))
    while (addSim.isReducible()) {
        println(addSim)
        println(addSim.isReducible())
        addSim = addSim.reduce()
    }
}