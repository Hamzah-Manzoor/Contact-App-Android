package com.example.myapplication.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

private val Typography.overline: Any
    get() {
        TODO("Not yet implemented")
    }
private val Typography.caption: Any
    get() {
        TODO("Not yet implemented")
    }
private val Typography.button: Any
    get() {
        TODO("Not yet implemented")
    }
private val Typography.body2: Any
    get() {
        TODO("Not yet implemented")
    }
private val Typography.body1: Any
    get() {
        TODO("Not yet implemented")
    }
private val Typography.subtitle2: Any
    get() {
        TODO("Not yet implemented")
    }
private val Typography.subtitle1: Any
    get() {
        TODO("Not yet implemented")
    }
private val Typography.h6: Any
    get() {
        TODO("Not yet implemented")
    }
private val Typography.h5: Any
    get() {
        TODO("Not yet implemented")
    }
private val Typography.h4: Any
    get() {
        TODO("Not yet implemented")
    }
private val Typography.h3: Any
    get() {
        TODO("Not yet implemented")
    }
private val Typography.h2: Any
    get() {
        TODO("Not yet implemented")
    }
private val Typography.h1: Any
    get() {
        TODO("Not yet implemented")
    }
private val Typography.defaultFontFamily: Any
    get() {
        TODO("Not yet implemented")
    }

@Composable
fun Typography(
    defaultFontFamily: Any,
    h1: Any,
    h2: Any,
    h3: Any,
    h4: Any,
    h5: Any,
    h6: Any,
    subtitle1: Any,
    subtitle2: Any,
    body1: Any,
    body2: Any,
    button: Any,
    caption: Any,
    overline: Any
): Typography {
    val defaultTypography = MaterialTheme.typography

    return Typography(
        defaultFontFamily = defaultTypography.defaultFontFamily,
        h1 = defaultTypography.h1.copy(fontSize = 30.sp, letterSpacing = 0.sp),
        h2 = defaultTypography.h2.copy(fontSize = 24.sp, letterSpacing = 0.sp),
        h3 = defaultTypography.h3.copy(fontSize = 20.sp, letterSpacing = 0.sp),
        h4 = defaultTypography.h4.copy(fontSize = 18.sp, letterSpacing = 0.sp),
        h5 = defaultTypography.h5.copy(fontSize = 16.sp, letterSpacing = 0.sp),
        h6 = defaultTypography.h6.copy(fontSize = 14.sp, letterSpacing = 0.15.sp),
        subtitle1 = defaultTypography.subtitle1.copy(fontSize = 16.sp, letterSpacing = 0.15.sp),
        subtitle2 = defaultTypography.subtitle2.copy(fontSize = 14.sp, letterSpacing = 0.1.sp),
        body1 = defaultTypography.body1.copy(fontSize = 16.sp, letterSpacing = 0.5.sp),
        body2 = defaultTypography.body2.copy(fontSize = 14.sp, letterSpacing = 0.25.sp),
        button = defaultTypography.button.copy(fontSize = 14.sp, letterSpacing = 1.25.sp),
        caption = defaultTypography.caption.copy(fontSize = 12.sp, letterSpacing = 0.4.sp),
        overline = defaultTypography.overline.copy(fontSize = 10.sp, letterSpacing = 1.5.sp)
    )
}

private fun Any.copy(fontSize: TextUnit, letterSpacing: TextUnit): Any {
    TODO("Not yet implemented")
}

@Composable
fun MyAndroidAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColorScheme(
            primary = Color(0xFF6200EE),
            //primaryVariant = Color(0xFF3700B3),
            secondary = Color(0xFF03DAC6),
            //secondaryVariant = Color(0xFF018786),
            background = Color(0xFFFFFFFF),
            surface = Color(0xFFFFFFFF),
            error = Color(0xFFB00020),
            onPrimary = Color(0xFFFFFFFF),
            onSecondary = Color(0xFF000000),
            onBackground = Color(0xFF000000),
            onSurface = Color(0xFF000000),
            onError = Color(0xFFFFFFFF)
            //isLight = true
        ),
        typography = Typography(
            MaterialTheme.typography.defaultFontFamily,
            MaterialTheme.typography.h1.copy(fontSize = 30.sp, letterSpacing = 0.sp),
            MaterialTheme.typography.h2.copy(fontSize = 24.sp, letterSpacing = 0.sp),
            MaterialTheme.typography.h3.copy(fontSize = 20.sp, letterSpacing = 0.sp),
            MaterialTheme.typography.h4.copy(fontSize = 18.sp, letterSpacing = 0.sp),
            MaterialTheme.typography.h5.copy(fontSize = 16.sp, letterSpacing = 0.sp),
            MaterialTheme.typography.h6.copy(fontSize = 14.sp, letterSpacing = 0.15.sp),
            MaterialTheme.typography.subtitle1.copy(fontSize = 16.sp, letterSpacing = 0.15.sp),
            MaterialTheme.typography.subtitle2.copy(fontSize = 14.sp, letterSpacing = 0.1.sp),
            MaterialTheme.typography.body1.copy(fontSize = 16.sp, letterSpacing = 0.5.sp),
            MaterialTheme.typography.body2.copy(fontSize = 14.sp, letterSpacing = 0.25.sp),
            MaterialTheme.typography.button.copy(fontSize = 14.sp, letterSpacing = 1.25.sp),
            MaterialTheme.typography.caption.copy(fontSize = 12.sp, letterSpacing = 0.4.sp),
            MaterialTheme.typography.overline.copy(fontSize = 10.sp, letterSpacing = 1.5.sp)
        )
    ) {
        content()
    }
}

fun ColorScheme(
    primary: Color,
    secondary: Color,
    background: Color,
    surface: Color,
    error: Color,
    onPrimary: Color,
    onSecondary: Color,
    onBackground: Color,
    onSurface: Color,
    onError: Color
): ColorScheme {
    TODO("Not yet implemented")
}


