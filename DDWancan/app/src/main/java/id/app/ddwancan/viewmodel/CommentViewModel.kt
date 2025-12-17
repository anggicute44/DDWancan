//package id.app.ddwancan.viewmodel
//
//import androidx.compose.runtime.State
//import androidx.compose.runtime.mutableStateOf
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import id.app.ddwancan.data.model.Comment
//import id.app.ddwancan.data.model.CommentRequest
//import id.app.ddwancan.data.network.RetrofitClient
//import kotlinx.coroutines.launch
//
//class CommentViewModel : ViewModel() {
//
//    private val _comments = mutableStateOf<List<Comment>>(emptyList())
//    val comments: State<List<Comment>> = _comments
//
//    private val _loading = mutableStateOf(false)
//    val loading: State<Boolean> = _loading
//
//    private val _error = mutableStateOf<String?>(null)
//    val error: State<String?> = _error
//
//    fun loadComments(articleId: String) {
//        viewModelScope.launch {
//            _loading.value = true
//            try {
//                _comments.value =
//                    RetrofitClient.commentService.getComments(articleId)
//            } catch (e: Exception) {
//                _error.value = e.message
//            } finally {
//                _loading.value = false
//            }
//        }
//    }
//
//    fun sendComment(
//        articleId: String,
//        userName: String,
//        message: String,
//        onDone: () -> Unit
//    ) {
//        viewModelScope.launch {
//            try {
//                RetrofitClient.commentService.postComment(
//                    request = CommentRequest( // Gunakan parameter 'request'
//                        articleId = articleId,
//                        name = userName, // Ubah 'userName' menjadi 'name' sesuai dengan CommentRequest
//                        message = message
//                    )
//                )
//                loadComments(articleId)
//                onDone()
//            } catch (e: Exception) {
//                _error.value = e.message
//            }
//        }
//    }
//}
