package com.aldajo92.opencvapplication.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.aldajo92.opencvapplication.R

@Preview
@Composable
fun RenderPhoto(
    modifier: Modifier = Modifier,
    imageSrc: String? = "",
    contentScale: ContentScale = ContentScale.FillWidth,
    onTakePhotoClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(20.dp)
            .clickable {
                onTakePhotoClick()
            },
        shape = RoundedCornerShape(14.dp),
    ) {
        Column {
            if (imageSrc.isNullOrEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ahColor_TIW()),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier
                            .size(60.dp, 60.dp)
                            .padding(5.dp),
                        painter = painterResource(R.drawable.ic_photo),
                        contentDescription = "",
                        contentScale = contentScale,
                    )
                }
            } else {
                Image(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.CenterHorizontally),
                    painter = rememberAsyncImagePainter(imageSrc),
                    contentDescription = "",
                    contentScale = contentScale,
                )
            }
        }

    }
}

@Composable
fun ahColor_TIW(darkModeColor: Color = Color(0xFFBACCF1)) =
    Color(0xFFBACCF1) orInDarkMode darkModeColor

@Composable
@ReadOnlyComposable
infix fun <T> T.orInDarkMode(other: T): T = if (!isDarkTheme()) this else other

@Composable
@ReadOnlyComposable
fun isDarkTheme() = isSystemInDarkTheme()