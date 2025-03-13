package net.alkafeel.questions

import MusicScreen
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.*
import net.alkafeel.questions.ui.theme.QuestionsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuestionsTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Screen.Home.route) {
                    composable(Screen.Home.route) {
                        HomeScreen(onCategoryClick = { course ->
                            // عند الضغط على تصنيف، يتم تمرير معرفه إلى شاشة الأسئلة
                            navController.navigate(Screen.Question.createRoute(course.id))
                        },
                            onMusicClick = { navController.navigate(Screen.Music.route) }
                        )
                    }
                    composable(Screen.Question.route) { backStackEntry ->
                        val categoryId = backStackEntry.arguments?.getString("categoryId")?.toInt() ?: 0
                        QuestionScreen(categoryId = categoryId, onBack = { navController.popBackStack() })
                    }

                    composable(Screen.Music.route) {
                        MusicScreen(onBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}

// تعريف مسارات التنقل
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Question : Screen("question/{categoryId}") {
        fun createRoute(categoryId: Int) = "question/$categoryId"
    }

    object Music : Screen("music")
}

/*----------------- شاشة التصنيفات (HomeScreen) -----------------*/
@Composable
fun HomeScreen(onCategoryClick: (CourseData) -> Unit, onMusicClick: () -> Unit) {
    // قائمة التصنيفات (على سبيل المثال)
    val courses = listOf(
        CourseData(
            id = 1,
            title = "التاريخ",
            lessonsCount = 35,
            progress = 0.75f,
            backgroundColor = Color(0xFFFFC107),
            iconRes = R.drawable.hieroglyph
        ),
        CourseData(
            id = 2,
            title = "الجغرافيا",
            lessonsCount = 30,
            progress = 0.50f,
            backgroundColor = Color(0xFF03A9F4),
            iconRes = R.drawable.history
        ),
        CourseData(
            id = 3,
            title = "الرياضيات",
            lessonsCount = 20,
            progress = 0.25f,
            backgroundColor = Color(0xFFC70039),
            iconRes = R.drawable.calculator
        ),
        CourseData(
            id = 4,
            title = "العلوم",
            lessonsCount = 20,
            progress = 0.25f,
            backgroundColor = Color(0xFF7d6608),
            iconRes = R.drawable.science
        )
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        HeaderSection(userName = "محسن", onMusicClick = onMusicClick)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "التصنيفات",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(courses) { course ->
                CourseCard(course = course, onClick = { onCategoryClick(course) })
            }
        }
    }
}

@Composable
fun HeaderSection(userName: String,onMusicClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = " مرحبا $userName,",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                fontSize = 20.sp
            )
            Text(
                text = "مرحبا بك مجدد!",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        // هنا يمكنك وضع صورة المستخدم من مجلد drawable
        Row(verticalAlignment = Alignment.CenterVertically) {
            // أيقونة الموسيقى للدخول إلى نافذة التحكم بالموسيقى
            IconButton(onClick = onMusicClick) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = "موسيقى")
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("صورة", color = Color.White)
            }
        }
    }
}

// تعريف بيانات التصنيف
data class CourseData(
    val id: Int,
    val title: String,
    val lessonsCount: Int,
    val progress: Float,
    val backgroundColor: Color,
    val iconRes: Int
)

@Composable
fun CourseCard(course: CourseData, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = course.backgroundColor),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.SpaceEvenly) {
                Text(
                    text = course.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = "${course.lessonsCount} عدد الاسئلة",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                )
                Spacer(modifier = Modifier.height(8.dp))
                CircularProgressWithLabel(progress = course.progress)
            }
            // هنا يمكنك عرض أيقونة التصنيف من resources
            Image(
                painter = painterResource(id = course.iconRes),
                contentDescription = course.title,
                modifier = Modifier.size(64.dp)
            )
        }
    }
}

@Composable
fun CircularProgressWithLabel(progress: Float) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(60.dp)
    ) {
        CircularProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxSize(),
            strokeWidth = 6.dp,
            color = Color.White
        )
        Text(
            text = "${(progress * 100).toInt()}%",
            color = Color.White,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

/*----------------- شاشة الأسئلة (QuestionScreen) -----------------*/
@Composable
fun QuestionScreen(categoryId: Int, onBack: () -> Unit) {

    var allQuestions by remember { mutableStateOf<List<Question>>(emptyList()) }
    var currentIndex by remember { mutableStateOf(0) }

    val context = LocalContext.current
    val soundManager = remember { SoundManager(context) }

    // تحميل الأسئلة من قاعدة البيانات وترتيبها عشوائيًا مع أخذ 30 سؤال كحد أقصى
    LaunchedEffect(categoryId) {
        val db = DatabaseProvider.getDatabase(context)
        val loadedQuestions = db.questionDao().getQuestionsForCategory(categoryId)
        allQuestions = loadedQuestions.shuffled().take(30)
        Log.d("QuestionScreen", "عدد الأسئلة المحملة: ${allQuestions.size}")
    }

    if (allQuestions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val currentQuestion = allQuestions[currentIndex]
        var selectedAnswer by remember { mutableStateOf<Answer?>(null) }
        var isAnswered by remember { mutableStateOf(false) }
        var answersForQuestion by remember { mutableStateOf<List<Answer>>(emptyList()) }

        // تحميل الإجابات الخاصة بالسؤال الحالي من جدول answers
        LaunchedEffect(currentQuestion.id) {
            val db = DatabaseProvider.getDatabase(context)
            answersForQuestion = db.answerDao().getAnswersForQuestion(currentQuestion.id).shuffled()
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.bg),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "السؤال ${currentIndex + 1} / ${allQuestions.size}",
                        color = Color.Green,
                        style = MaterialTheme.typography.titleMedium
                    )
                    TextButton(onClick = onBack) {
                        Text("رجوع", color = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = currentQuestion.questionText,
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.White, fontSize = 20.sp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                answersForQuestion.forEach { answer ->
                    val backgroundColor = when {
                        isAnswered && answer.isCorrect -> Color(0xFF4CAF50)
                        isAnswered && selectedAnswer?.id == answer.id && !answer.isCorrect -> Color(0xFFD32F2F)
                        else -> MaterialTheme.colorScheme.surface
                    }
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = backgroundColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable(enabled = !isAnswered) {
                                selectedAnswer = answer
                                isAnswered = true
                                if (answer.isCorrect) {
                                    soundManager.playCorrect()
                                } else {
                                    soundManager.playIncorrect()
                                }
                            }
                    ) {
                        Text(
                            text = answer.answerText,
                            modifier = Modifier.padding(16.dp),
                            color = if (isAnswered && (answer.isCorrect || selectedAnswer?.id == answer.id)) Color.White else Color.Black
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                if (isAnswered) {
                    Button(
                        onClick = {
                            if (currentIndex < allQuestions.size - 1) {
                                currentIndex++
                                selectedAnswer = null
                                isAnswered = false
                            } else {

                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("التالي")
                    }
                }
            }
        }
    }
}
