package ru.yandexpraktikum.cardsanimation.compose

import android.util.Log
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import ru.yandexpraktikum.cardsanimation.HORIZONTAL_DRAG_THRESHOLD
import ru.yandexpraktikum.cardsanimation.VERTICAL_DRAG_THRESHOLD
import ru.yandexpraktikum.cardsanimation.model.CardData
import kotlin.math.abs

/**
 * Метод для вычисления поворота карты в конкретной позиции
 */
fun calculateCardRotation(
    cardIndex: Int,
    cardCount: Int,
    isRotated: Boolean
): Float {
    if (cardCount <= 1) return 0f

    return if (isRotated) {
        val angleStep = 180f / (cardCount - 1)
        90f - (cardIndex * angleStep)
    } else {
        val angleStep = 45f / (cardCount - 1)
        22.5f - (cardIndex * angleStep)
    }
}

data class CardSwapAnimationState(
    val isAnimating: Boolean = false,
    val animationStep: Int = 0
)

@Composable
fun AnimatedCardStack(cards: List<CardData>) {
    val cardCount = cards.size
    var isRotated by remember { mutableStateOf(false) }

    // TODO: [Задание 2] Добавьте обработку жестов
    // Подсказка: Используйте Modifier.pointerInput() с методом detectDragGestures()

    var horizontalDragAmount by remember { mutableFloatStateOf(0f) }
    var verticalDragAmount by remember { mutableFloatStateOf(0f) }
    var animationState by remember { mutableStateOf(CardSwapAnimationState()) }
    var cardsList by remember { mutableStateOf(cards) }
    Box(
        modifier = Modifier
            .pointerInput(Unit) {
                // Понимаю, что можно использовать detectVerticalDragGestures, но
                // по заданию вроде надо было использовать универсальный обработчик
                detectDragGestures(
                    onDragStart = {},
                    onDragEnd = {
                        if (!animationState.isAnimating) {
                            val horizontalOffset = abs(horizontalDragAmount)
                            val verticalOffset = abs(verticalDragAmount)
                            val scrollIsVertical = verticalOffset > horizontalOffset
                            val scrollIsHorizontal = horizontalOffset > verticalOffset
                            if (scrollIsHorizontal && horizontalOffset > HORIZONTAL_DRAG_THRESHOLD) {
                                animationState = CardSwapAnimationState(true, 1)
                            } else if (scrollIsVertical && verticalOffset > VERTICAL_DRAG_THRESHOLD) {
                                isRotated = verticalDragAmount > 0
                            }
                        }
                        horizontalDragAmount = 0f
                        verticalDragAmount = 0f
                    }
                ) { _, dragAmount ->
                    horizontalDragAmount -= dragAmount.x
                    verticalDragAmount -= dragAmount.y
                }
            },
        contentAlignment = Alignment.Center
    ) {
        val handle = remember {
            { step: Int ->
                Log.d("ANIM", "step $step")
                when (step) {
                    1 -> animationState = CardSwapAnimationState(true, 2)
                    2 -> animationState = CardSwapAnimationState(true, 3)
                    3 -> {
                        animationState = CardSwapAnimationState()
                        cardsList = reorderCards(cardsList)
                    }
                }
            }
        }
        cardsList.forEachIndexed { i, cardData ->
            key(cardData.imageResId) {
                val targetRotation = with (animationState) {
                    val rotationIndex = if (isAnimating && animationStep == 3) {
                        (i - 1).mod(cardsList.size)
                    } else i
                    calculateCardRotation(rotationIndex, cardCount, isRotated)
                }

                AnimatedCard(
                    cardIndex = i,
                    targetRotation = targetRotation,
                    cardData = cardData,
                    // TODO: [Задание 5] Здесь добавьте параметры анимации карты
                    isAnimating = i == 0,
                    animationStep = animationState.animationStep,
                    onAnimationStepComplete = handle
                )
            }
        }
    }
}



// Простая функция перестановки карт
fun reorderCards(cards: List<CardData>): List<CardData> {
    return cards.drop(1) + cards.first()
}