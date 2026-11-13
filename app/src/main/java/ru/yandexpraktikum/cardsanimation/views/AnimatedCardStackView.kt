package ru.yandexpraktikum.cardsanimation.views

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.FrameLayout
import ru.yandexpraktikum.cardsanimation.HORIZONTAL_DRAG_THRESHOLD
import ru.yandexpraktikum.cardsanimation.VERTICAL_DRAG_THRESHOLD
import ru.yandexpraktikum.cardsanimation.model.CardData
import kotlin.math.abs
import kotlin.math.pow

class AnimatedCardStackView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var cardDataList: List<CardData> = emptyList()
    private val cards = mutableListOf<AnimatedCardView>()
    private var isRotated = false
    private var verticalDragAmount = 0f
    private var horizontalDragAmount = 0f

    private var isAnimating = false
    private var animationStep = 0


    private val gestureDetector = GestureDetector(context, object: GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            e1?.let {
                verticalDragAmount += e2.y - e1.y
                horizontalDragAmount += e2.x - e1.x
            }
            return true
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            verticalDragAmount += distanceY
            horizontalDragAmount += distanceX
            return true
        }
    })

    fun setCards(newCardDataList: List<CardData>) {
        cardDataList = newCardDataList
        setupCards()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP &&
            verticalDragAmount.pow(2) + horizontalDragAmount.pow(2) > 0) {
            handleDragEnd()
            verticalDragAmount = 0f
            horizontalDragAmount = 0f
            return true
        }
        return gestureDetector.onTouchEvent(event)
    }

    private fun handleDragEnd() {
        val absoluteVertical = abs(verticalDragAmount)
        val absoluteHorizontal = abs(horizontalDragAmount)
        if (absoluteVertical > VERTICAL_DRAG_THRESHOLD && absoluteVertical > absoluteHorizontal) {
            isRotated = verticalDragAmount > 0
            updateCardPositions(true)
        } else if (absoluteHorizontal > HORIZONTAL_DRAG_THRESHOLD && absoluteHorizontal > absoluteVertical) {
            startCardSwapAnimation(cards.first())
        }
    }

    private fun setupCards() {
        clearCards()
        cardDataList.forEachIndexed { index, cardData ->
            val cardView = AnimatedCardView(context).apply {
                setCardData(cardData)
                setStackPosition(index)
            }
            cards.add(cardView)
            addView(cardView)
        }
        // Возврат в исходное положение
        isRotated = false
        updateCardPositions()
    }

    private fun clearCards() {
        cards.clear()
        removeAllViews()
    }

    private fun getBaseRotation(idx: Int): Float {
        return if (cards.size > 1) {
            val angleStep = 45f / (cards.size - 1)
            22.5f - (idx * angleStep)
        } else {
            0f
        }
    }

    private fun updateCardPositions(animate: Boolean = false) {
        val cardCount = cards.size

        cards.forEachIndexed { index, cardView ->
            // Расчёт расположения карт в исходной позиции

            // Расчёт финальной позиции (для эффекта раскрытой колоды карт)
            val targetRotation = if (isRotated) {
                val angleStep = if (cardCount > 1) 180f / (cardCount - 1) else 0f
                90f - (index * angleStep)
            } else getBaseRotation(index)

            val cardWidth = 100f * resources.displayMetrics.density
            val cardHeight = 160f * resources.displayMetrics.density
            val sharedX = width / 2f - cardWidth / 2f
            val sharedY = height / 2f - cardHeight / 2f

            cardView.x = sharedX
            cardView.y = sharedY

            cardView.pivotX = cardWidth / 2f
            cardView.pivotY = cardHeight

            // TODO: [Задание 1] Замените на метод, который анимирует движение карты
            if (animate) cardView.animateCardRotation(targetRotation)
            else cardView.rotation = targetRotation
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            updateCardPositions()
        }
    }

    private fun startCardSwapAnimation(bottomCard: AnimatedCardView) {
        // TODO: [Задание 5] Добавьте анимацию перетасовки карт
        // На данном этапе просто быстро двигаем нижнюю карту наверх
        if (isAnimating) return

        isAnimating = true
        animationStep = 1

        with (bottomCard) {
            moveCardRight {
                animationStep = 2
                moveCardToTop {
                    animationStep = 3
                    cards.forEachIndexed  { idx, view ->
                        if (idx != 0) {
                            view.animateCardRotation(
                                getBaseRotation((idx - 1).mod(cards.size))
                            )
                        }
                    }
                    moveCardToFinalPosition(
                        getBaseRotation(cards.size - 1)
                    ) {
                        isAnimating = false
                        animationStep = 0
                        cardDataList = reorderCards(cardDataList)
                        setupCards()
                    }
                }
            }
        }
    }

    // Простая функция перестановки карт
    fun reorderCards(cards: List<CardData>): List<CardData> {
        return cards.drop(1) + cards.first()
    }
    // TODO: [Задание 2] Добавьте обработку жестов
    // Подсказка: Используйте GestureDetector с методом onFling для обработки свайпов

    // TODO: [Задание 3] Добавьте обработку вертикальных свайпов (вверх/вниз)

    // TODO: [Задание 4] Добавьте обработку горизонтальных свайпов (влево/вправо)
}