package org.d3if3138.mobpro1.asesmen3fida

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.d3if3138.mobpro1.asesmen3fida.system.database.InstantGramViewModel
import org.d3if3138.mobpro1.asesmen3fida.system.database.model.User
import org.d3if3138.mobpro1.asesmen3fida.system.navigation.NavigationGraph
import org.d3if3138.mobpro1.asesmen3fida.system.network.UserDataStore
import org.d3if3138.mobpro1.asesmen3fida.system.network.signIn
import org.d3if3138.mobpro1.asesmen3fida.system.network.signOut
import org.d3if3138.mobpro1.asesmen3fida.system.utils.SettingsDataStore
import org.d3if3138.mobpro1.asesmen3fida.ui.component.getCroppedImage
import org.d3if3138.mobpro1.asesmen3fida.ui.theme.Asesmen3FidaTheme
import org.d3if3138.mobpro1.asesmen3fida.ui.widgets.AddForm
import org.d3if3138.mobpro1.asesmen3fida.ui.widgets.ProfilDialog
import org.d3if3138.mobpro1.asesmen3fida.ui.widgets.TopAppBarWidget

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BaseApp()
        }
    }
}

@Composable
fun BaseApp(
    navController: NavHostController = rememberNavController(),
    context: Context = LocalContext.current,
    instaViewModel: InstantGramViewModel = viewModel()
) {
    val dataStore = SettingsDataStore(context)
    val userStore = UserDataStore(context)
    val appTheme by dataStore.layoutFlow.collectAsState(true)
    var showDialog by remember { mutableStateOf(false) }
    val user by userStore.userFlow.collectAsState(User())

    var shownImage by rememberSaveable { mutableStateOf(false) }
    var bitmap: Bitmap? by remember { mutableStateOf(null) }
    val launcher = rememberLauncherForActivityResult(contract = CropImageContract()) {
        bitmap = getCroppedImage(context.contentResolver, it)
        if (bitmap != null) shownImage = true
    }

    Asesmen3FidaTheme(darkTheme = appTheme) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            topBar = {
                TopAppBarWidget(
                    title = stringResource(id = R.string.app_name),
                    user = user,
                    appTheme = appTheme,
                    showDialog = showDialog,
                    onShowDialogChange = { showDialog = it },
                    onAppThemeChange = { newTheme ->
                        CoroutineScope(Dispatchers.IO).launch {
                            dataStore.saveLayout(!appTheme)
                        }
                    }
                )
            },
            bottomBar = {
                //BottomBarWidget(navController)
            },
            ////////////////////////////////////
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = { Text(stringResource(id = R.string.addReview)) },
                    icon = {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = stringResource(id = R.string.addReview)
                        )
                    },
                    onClick = {
                        val options = CropImageContractOptions(
                            null, CropImageOptions(
                                imageSourceIncludeGallery = true,
                                imageSourceIncludeCamera = true,
                                fixAspectRatio = true
                            )
                        )
                        launcher.launch(options)
                    }
                )
            }
            ////////////////////////////////////

        ) { paddingValues ->
            Modifier.padding(paddingValues)
            //NavigationGraph(navController, apiProfile, modifier = Modifier.padding(paddingValues))
            NavigationGraph(navController, Modifier.padding(paddingValues), instaViewModel, user)

            // LaunchedEffect to handle sign-in if needed
            LaunchedEffect(showDialog) {
                if (showDialog && user.email.isEmpty()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        signIn(context, userStore)
                    }
                }
            }

            // Display the dialog if showDialog is true
            if (showDialog && user.email.isNotEmpty()) {
                ProfilDialog(user = user, onDismissRequest = { showDialog = false }) {
                    CoroutineScope(Dispatchers.IO).launch {
                        signOut(context, userStore)
                    }
                    showDialog = false
                }
            }

            if (shownImage) {
                AddForm(bitmap = bitmap, onDismissRequest = { shownImage = false }, onConfirmation = { animeReview  ->
                    // Do something
                    //systemViewModel.addAnime(user.email, animeTitle, animeReview, bitmap!!)
                    instaViewModel.doPost(user.email, user.email, user.name, user.photoUrl, animeReview, bitmap!!)
                    shownImage = false
                })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Asesmen3FidaTheme {
        BaseApp()
    }
}