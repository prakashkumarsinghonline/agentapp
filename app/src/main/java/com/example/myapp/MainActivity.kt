package com.example.myapp

import androidx.compose.foundation.layout.PaddingValues
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

data class Video(
    val id: String,
    val title: String,
    val thumbnail: String,
    val published: String,
    val views: String,
    val description: String
)

private val Black = Color(0xFF000000)
private val White = Color(0xFFFFFFFF)
private val Gray100 = Color(0xFFF5F5F5)
private val Gray200 = Color(0xFFEEEEEE)
private val Gray400 = Color(0xFFBDBDBD)
private val Gray600 = Color(0xFF757575)
private val Gray800 = Color(0xFF424242)

fun fetchRssFeed(): List<Video> {
    val url = URL("https://www.youtube.com/feeds/videos.xml?channel_id=UCvgu3umoosQwH-1LJpu-iEQ")
    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "GET"
    connection.connectTimeout = 10000
    connection.readTimeout = 10000
    val xml = connection.inputStream.bufferedReader().readText()
    connection.disconnect()
    return parseRss(xml)
}

fun parseRss(xml: String): List<Video> {
    val videos = mutableListOf<Video>()
    val factory = XmlPullParserFactory.newInstance()
    val parser = factory.newPullParser()
    parser.setInput(StringReader(xml))
    var eventType = parser.eventType
    var id = ""
    var title = ""
    var thumbnail = ""
    var published = ""
    var description = ""
    var inEntry = false
    var currentTag = ""
    while (eventType != XmlPullParser.END_DOCUMENT) {
        when (eventType) {
            XmlPullParser.START_TAG -> {
                currentTag = parser.name
                if (currentTag == "entry") { inEntry = true; id=""; title=""; thumbnail=""; published=""; description="" }
            }
            XmlPullParser.TEXT -> {
                val text = parser.text
                if (inEntry) {
                    when (currentTag) {
                        "yt:videoId" -> id = text
                        "title" -> if (title.isEmpty()) title = text
                        "published" -> published = text
                    }
                }
            }
            XmlPullParser.END_TAG -> {
                if (parser.name == "media:thumbnail" && inEntry && thumbnail.isEmpty()) {
                    thumbnail = parser.getAttributeValue(null, "url") ?: ""
                }
                if (parser.name == "entry") {
                    if (id.isNotEmpty() && title.isNotEmpty()) {
                        val formattedDate = try {
                            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US)
                            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)
                            val date = inputFormat.parse(published)
                            if (date != null) outputFormat.format(date) else published.take(10)
                        } catch (e: Exception) { published.take(10) }
                        val thumb = "https://i.ytimg.com/vi/$id/hqdefault.jpg"
                        videos.add(Video(id, title, thumb, formattedDate, "", description))
                    }
                    inEntry = false
                }
                currentTag = ""
            }
        }
        eventType = parser.next()
    }
    return videos
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Black,
                    onPrimary = White,
                    secondary = Gray600,
                    onSecondary = White,
                    background = White,
                    onBackground = Black,
                    surface = White,
                    onSurface = Black,
                    surfaceVariant = Gray100,
                    onSurfaceVariant = Gray800,
                    outline = Gray400
                )
            ) {
                BankingBabaApp()
            }
        }
    }
}

@Composable
fun BankingBabaApp() {
    val videos = remember { mutableStateOf(emptyList<Video>()) }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>("") }
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        scope.launch {
            isLoading.value = true
            errorMessage.value = ""
            try {
                val result = withContext(Dispatchers.IO) { fetchRssFeed() }
                videos.value = result
            } catch (e: Exception) {
                errorMessage.value = "Could not load videos. Check your internet connection."
            }
        }
    }
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth().background(Black).statusBarsPadding().padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text("Banking Baba", color = White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("by Rakesh", color = Gray400, fontSize = 13.sp)
            }
        },
        containerColor = White
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).background(White)) {
            ChannelInfoBar(modifier = Modifier.fillMaxWidth().background(Gray100).padding(horizontal = 16.dp, vertical = 12.dp))
            when {
                isLoading.value -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Black)
                }
                errorMessage.value != null && errorMessage.value!!.isNotEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(errorMessage.value!!, color = Gray600, fontSize = 16.sp)
                }
                videos.value.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No videos available", color = Gray600, fontSize = 16.sp)
                }
                else -> {
                    Text("Latest Videos", modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp), color = Black, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(videos.value) { video -> VideoCard(video) }
                    }
                }
            }
        }
    }
}

@Composable
fun ChannelInfoBar(modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(48.dp).clip(RoundedCornerShape(24.dp)).background(Black), contentAlignment = Alignment.Center) {
            Text("B", color = White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text("Rakesh", color = Black, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text("Personal Finance Educator · 481K subscribers", color = Gray600, fontSize = 12.sp)
        }
        Spacer(Modifier.weight(1f))
        Text("Subscribe", color = Black, fontSize = 12.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(Gray200).padding(horizontal = 12.dp, vertical = 6.dp).clickable { })
    }
}

@Composable
fun VideoCard(video: Video) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth().clickable {
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://www.youtube.com/watch?v=${video.id}"))
            context.startActivity(intent)
        },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Gray100),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(context).data(video.thumbnail).crossfade(true).build(),
                contentDescription = video.title,
                modifier = Modifier.fillMaxWidth().height(180.dp).background(Gray200),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(video.title, color = Black, fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Text("Banking Baba · ${video.published}", color = Gray600, fontSize = 12.sp)
            }
        }
    }
}
