package com.astro.app.core.designsystem.celestial

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.Dp
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.texture.AstroLight
import com.astro.app.core.designsystem.texture.drawSphereBody
import com.astro.app.core.designsystem.texture.drawSphereHalo
import com.astro.app.core.designsystem.texture.drawSphereInnerShadow
import com.astro.app.core.designsystem.texture.rememberGrainBrush

private const val REGOLITH_INTENSITY = 0.10f
private const val REGOLITH_SCALE = 2.4f

/**
 * La Lune, rendue comme une surface : sphère éclairée par la source commune de
 * l'application, creusée par une ombre interne, couverte d'un grain de régolithe et
 * marquée de cratères en relief.
 *
 * Composant purement visuel, sans interaction : il sert aussi bien au raccourci de la
 * page d'accueil qu'aux écrans d'information. Une seule définition de la matière, donc
 * une Lune identique partout.
 */
@Composable
fun MoonOrb(size: Dp, modifier: Modifier = Modifier) {
    val palette = AstroTheme.palette
    val regolith = rememberGrainBrush(CelestialMaterial.MoonGrain, REGOLITH_INTENSITY)
    val craters = remember { CraterField.generate() }

    Canvas(modifier = modifier.size(size)) {
        val boxRadius = this.size.minDimension / 2f
        val radius = boxRadius * 0.62f
        val center = Offset(this.size.width / 2f, this.size.height / 2f)

        drawSphereHalo(center, radius, palette.accentLunar.copy(alpha = 0.30f), spread = boxRadius / radius)
        drawSphereBody(
            center = center,
            radius = radius,
            lit = CelestialMaterial.MoonLit,
            shade = CelestialMaterial.MoonShade,
        )
        scale(REGOLITH_SCALE, REGOLITH_SCALE, pivot = center) {
            drawCircle(brush = regolith, radius = radius / REGOLITH_SCALE, center = center)
        }
        craters.forEach { drawCrater(it, center, radius, CelestialMaterial.MoonShadow, Color.White) }
        drawSphereInnerShadow(center, radius, CelestialMaterial.MoonShadow.copy(alpha = 0.80f))
    }
}

/**
 * Un cratère se lit par deux arcs et non par un cercle : ombre du côté de la lumière,
 * bord éclairé du côté opposé. C'est l'inverse de l'intuition, et c'est ce qui creuse.
 */
private fun DrawScope.drawCrater(
    crater: Crater,
    center: Offset,
    radius: Float,
    shadow: Color,
    light: Color,
) {
    val position = Offset(center.x + crater.x * radius, center.y + crater.y * radius)
    val craterRadius = crater.radius * radius
    val offset = craterRadius * 0.34f

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(shadow.copy(alpha = 0.34f * crater.depth), Color.Transparent),
            center = Offset(position.x + offset * AstroLight.X, position.y + offset * AstroLight.Y),
            radius = craterRadius,
        ),
        radius = craterRadius,
        center = position,
    )
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(light.copy(alpha = 0.20f * crater.depth), Color.Transparent),
            center = Offset(position.x - offset * AstroLight.X, position.y - offset * AstroLight.Y),
            radius = craterRadius * 0.9f,
        ),
        radius = craterRadius,
        center = position,
    )
}
