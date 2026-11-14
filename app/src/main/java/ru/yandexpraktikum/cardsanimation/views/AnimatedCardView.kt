package ru.yandexpraktikum.cardsanimation.views

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.cardview.widget.CardView
import ru.yandexpraktikum.cardsanimation.R
import ru.yandexpraktikum.cardsanimation.model.CardData
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class AnimatedCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    val cardView: CardView
    private val cardImageView: ImageView
    private val moveRightDistance = 50f * resources.displayMetrics.density
    private val rotationRadians: Float
        get() = this.rotation / 180f * PI.toFloat()

    init {
        LayoutInflater.from(context).inflate(R.layout.card_view, this, true)

        cardView = this.getChildAt(0) as CardView
        cardImageView = findViewById(R.id.cardImage)
        // здесь width = height = 0... зачем здесь pivotx = ...?
        pivotX = width / 2f
        pivotY = height.toFloat()
    }

    // чет здесь не то, после этого метода startCardSwapAnimation начинает вращать не туда
    // похоже, что w и h возвращают какие-то другие значения
//    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
//        super.onSizeChanged(w, h, oldw, oldh)
//        pivotX = w / 2f
//        pivotY = h.toFloat()
//    }

    fun setCardData(cardData: CardData) {
        cardImageView.setImageResource(cardData.imageResId)
    }

    fun moveCardRight(onComplete: () -> Unit = {}) {
        this.animate()
            .translationXBy(moveRightDistance * cos(rotationRadians))
            .translationYBy(moveRightDistance * sin(rotationRadians))
            .withEndAction(onComplete)
            .start()
    }

    fun moveCardToTop(onComplete: () -> Unit = {}) {
        this.animate()
            .translationZBy(1000f)
            .translationXBy(-moveRightDistance * cos(rotationRadians))
            .translationYBy(-moveRightDistance * sin(rotationRadians))
            .withEndAction(onComplete)
            .start()
    }

    fun moveCardToFinalPosition(
        finalRotation: Float,
        onComplete: () -> Unit = {}
    ) {
        this.animate()
            .rotation(finalRotation)
            .withEndAction(onComplete)
            .start()
    }

    fun setStackPosition(index: Int) {
        cardView.cardElevation = (4 + index * 1).toFloat() * resources.displayMetrics.density
    }

    // TODO: [Задание 1] Добавьте методы для анимации
    // Подсказка: используйте ObjectAnimator для плавной анимации
    fun animateCardRotation(targetAngle: Float) {
        ObjectAnimator.ofFloat(this, "rotation", targetAngle).start()
    }

    // TODO: [Задание 1] Добавьте метод для анимации поворота карты (чтобы был плавный эффект раскрытия/закрытия колоды)
    // fun animateToRotation(targetRotation: Float, duration: Long = 300) { ... }

    // TODO: [Задание 5, шаг 1] Добавьте метод для анимации перетасовки карт (первым шагом нижняя карта двигается вправо)
    // fun moveCardRight(onComplete: (() -> Unit)? = null) { ... }

    // TODO: [Задание 5, шаг 2] Добавьте метод для анимации выдвижения нижней карты наверх
    // fun moveCardToTop(onComplete: (() -> Unit)? = null) { ... }

    // TODO: [Задание 5, шаг 3] Добавьте анимацию перемещения всей колоды карты в желаемую позицию
    // fun adjustToFinalPosition(finalRotation: Float, finalZOrder: Int, onComplete: (() -> Unit)? = null) { ... }
} 