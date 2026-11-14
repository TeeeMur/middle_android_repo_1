package ru.yandexpraktikum.cardsanimation.compose

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import ru.yandexpraktikum.cardsanimation.model.CardData
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sign
import kotlin.math.sin

@Composable
fun AnimatedCard(
    cardIndex: Int,
    cardData: CardData,
    isAnimating: Boolean,
    animationStep: Int,
    targetRotation: Float,
    onAnimationStepComplete: (Int) -> Unit = {}
) {
    // TODO: [Задание 1] Добавьте анимацию поворота карты
    // Подсказка: используйте animateFloatAsState для плавной анимации
    val animatedRotationState by animateFloatAsState(
        targetRotation,
        animationSpec = tween(durationMillis = if (animationStep == 3) 800 else 300),
        finishedListener = {
            if (isAnimating && animationStep == 3) onAnimationStepComplete(3)
        }
    )

    val density = LocalDensity.current
    val translationDistance = remember { with (density) { 50.dp.toPx()} }
    val targetRadians = remember(targetRotation) { (targetRotation) / 180 * PI }
    val animatedTranslationX by animateFloatAsState(
        if (isAnimating && animationStep == 1) {
            translationDistance * (cos(targetRadians) * sign(targetRadians)).toFloat()
        } else 0f,
        animationSpec = tween(durationMillis = if (animationStep == 3) 800 else 300),
        finishedListener = {
            if (isAnimating) onAnimationStepComplete(animationStep)
        }
    )

    val animatedTranslationY by animateFloatAsState(
        if (isAnimating && animationStep == 1) {
            translationDistance * (sin(targetRadians) * sign(targetRadians)).toFloat()
        } else 0f,
        animationSpec = tween(durationMillis = if (animationStep == 3) 800 else 300),
    )

    val shouldBringToFront = isAnimating && animationStep >= 2

    Card(
        modifier = Modifier
            .size(width = 100.dp, height = 160.dp)
            // TODO: [Задание 5] Добавьте анимацию карты при свайпе вправо или влево
            .graphicsLayer {
                rotationZ = animatedRotationState
                transformOrigin = TransformOrigin(0.5f, 1.0f)
                if (isAnimating) {
                    translationX = animatedTranslationX
                    translationY = animatedTranslationY
                }
            }.let { modifier ->
                if (shouldBringToFront) modifier.zIndex(1000f)
                else modifier
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = (4 + cardIndex).dp
        )
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(cardData.imageResId),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
    }
}