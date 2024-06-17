package org.d3if3138.mobpro1.asesmen3fida.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.d3if3138.mobpro1.asesmen3fida.R
import org.d3if3138.mobpro1.asesmen3fida.system.database.InstantGramViewModel
import org.d3if3138.mobpro1.asesmen3fida.system.database.model.User
import org.d3if3138.mobpro1.asesmen3fida.system.network.InstantGramStatus
import org.d3if3138.mobpro1.asesmen3fida.ui.component.ItemDisplay

@Composable
fun PublicPost(postModel: InstantGramViewModel, modifier: Modifier = Modifier, user: User) {
    val postStatus by postModel.InstantGram_status.collectAsState()
    val posts by postModel.posts.observeAsState()

    val likes by postModel.likes.observeAsState()

    when (postStatus) {
        InstantGramStatus.LOADING -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Loading...",
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        /*InstantGramStatus.SUCCESS -> {
            LazyVerticalGrid(
                modifier = modifier
                    .fillMaxSize()
                    .padding(4.dp),
                columns = GridCells.Fixed(2)
            ) {
                val filterLike = posts?.filter { it.email == user.email }
                items(filterLike!!) { likes ->
                    if (likes.)
                }

                items(posts!!) { post ->
                    ItemDisplay(post, onDelete = { postModel.delPost(post.id) }, onLike = { postModel.likePost(post.id, user.email) }, isLike = filterLike?.contains(post) == true)
                }
            }
        }*/

        InstantGramStatus.SUCCESS -> {
            LazyVerticalGrid(
                modifier = modifier
                    .fillMaxSize()
                    .padding(4.dp),
                columns = GridCells.Fixed(1)
            ) {
                /*val filterLike = posts?.filter { post ->
                    likes?.any { it.userId == post.id } == true
                } ?: emptyList()

                items(posts ?: emptyList()) { post ->
                    val isLiked = filterLike.contains(post)
                    ItemDisplay(
                        post,
                        isLike = isLiked,
                        onDelete = { postModel.delPost(post.id) },
                        onLike = { postModel.likePost(post.id, user.email) }
                    )
                }*/
                val filterLike = posts?.filter { post ->
                    likes?.any { it.postId == post.id && it.userId == user.email } == true
                } ?: emptyList()

                items(posts ?: emptyList()) { post ->
                    val isLiked = filterLike.contains(post)
                    val delete = post.email == user.email
                    ItemDisplay(
                        post,
                        delete,
                        isLike = isLiked,
                        onDelete = { postModel.delPost(post.id) },
                        onLike = { postModel.likePost(post.id, user.email) }
                    )
                }
            }
        }


        /*InstantGramStatus.SUCCESS -> {
            LazyVerticalGrid(
                modifier = modifier
                    .fillMaxSize()
                    .padding(4.dp),
                columns = GridCells.Fixed(2)
            ) {
                val filterLike = posts?.filter { post ->
                    likes?.any { it.postId == post.id } == true
                }

                items(posts ?: emptyList()) { post ->
                    val isLiked = filterLike?.contains(post) == true
                    ItemDisplay(post, isLike = isLiked, onDelete = { postModel.delPost(post.id) }, onLike = { postModel.likePost(post.id, user.email) })
                }
            }
        }*/

        InstantGramStatus.FAILED -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = stringResource(id = R.string.error))
                Button(
                    onClick = { postModel.getPosts() },
                    modifier = Modifier.padding(top = 16.dp),
                    contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
                ) {
                    Text(text = stringResource(id = R.string.try_again))
                }
            }

        }

        null -> TODO()
    }

}