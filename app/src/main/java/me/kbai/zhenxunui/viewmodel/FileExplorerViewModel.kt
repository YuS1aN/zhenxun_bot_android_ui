package me.kbai.zhenxunui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.kbai.zhenxunui.extends.apiCollect
import me.kbai.zhenxunui.model.EditRemoteFile
import me.kbai.zhenxunui.model.RemoteFile
import me.kbai.zhenxunui.repository.ApiRepository
import me.kbai.zhenxunui.repository.Resource

/**
 * @author Sean on 2023/6/10
 */
class FileExplorerViewModel : ViewModel() {
    var file: RemoteFile? = null

    private val _dir: MutableLiveData<Resource<List<RemoteFile>>> = MutableLiveData()
    val dir: LiveData<Resource<List<RemoteFile>>> = _dir

    fun readDir() = viewModelScope.launch {
        ApiRepository.readDir(file?.getPath().orEmpty())
            .apiCollect {
                _dir.value = it
            }
    }

    fun deleteFile(file: RemoteFile) = ApiRepository.deleteFile(file)

    fun renameFile(file: RemoteFile, newName: String) = ApiRepository.renameFile(file, newName)

    fun createNewFile(name: String, parent: String) =
        ApiRepository.createNewFile(RemoteFile(true, name, parent))

    fun createNewFolder(name: String, parent: String) =
        ApiRepository.createNewFolder(RemoteFile(false, name, parent))

    fun readFile(file: RemoteFile) = ApiRepository.readFile(file)

    fun editFile(file: RemoteFile, content: String) =
        ApiRepository.editFile(EditRemoteFile(file.getPath(), content))
}