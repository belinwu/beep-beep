package presentation.order

import domain.entity.Order
import domain.entity.OrderMeal
import util.OrderState

data class OrderScreenUiState(
    val orders: List<OrderUiState> = emptyList(),
    val totalOrders: Int = 0,
)

data class OrderUiState(
    val orderMealUiStates: List<OrderMealUiState> = emptyList(),
    val totalPrice: Double = 0.0,
    val orderState: Int = OrderState.PENDING.statusCode,
)

data class OrderMealUiState(
    val mealImageUrl: String = "",
    val mealName: String = "",
    val quantity: Int = 0,
)

fun OrderMeal.toOrderMealUiState(): OrderMealUiState {
    return OrderMealUiState(
        mealName = mealName,
        mealImageUrl = mealImageUrl,
        quantity = quantity
    )
}

//fun List<OrderMeal>.toOrderMealUiState():List<OrderMealsUiState> = map { it.quantity = this }
fun Order.toOrderUiState(): OrderUiState {
    return OrderUiState(
        orderMealUiStates = meals.map { it.toOrderMealUiState() }
    )
}

