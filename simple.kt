sealed class SimType {
    fun isReducible(): Boolean {
        return when (this) {
            is NumberSim -> false
            else -> true
        }
    }

    open fun reduce(): SimType {
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

    override fun reduce(): SimType {
        return when {
            left.isReducible() -> MultiplySim(left.reduce(), right)
            right.isReducible() -> MultiplySim(left, right.reduce())
            else -> NumberSim(left.value() * right.value())
        }
    }
}

class Machine(expr: SimType) {
    private var expr = expr
    private fun step() {
        expr = expr.reduce()
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
    val testExpr: SimType = AddSim(
        MultiplySim(NumberSim(1), NumberSim(2)),
        MultiplySim(NumberSim(3), NumberSim(4))
    )
    val vm = Machine(testExpr)
    vm.run()
}