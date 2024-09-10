package com.fsck.k9.ui.choosefolder

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import app.k9mail.legacy.account.Account
import app.k9mail.legacy.account.Account.FolderMode
import app.k9mail.legacy.folder.DisplayFolder
import app.k9mail.legacy.mailstore.DisplayFolderRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ChooseFolderViewModel(
    private val folderRepository: DisplayFolderRepository,
) : ViewModel() {
    private val inputFlow = MutableSharedFlow<DisplayMode>(replay = 1)
    private val foldersFlow = inputFlow
        .flatMapLatest { (account, displayMode) ->
            folderRepository.getDisplayFoldersFlow(account, displayMode)
        }

    var currentDisplayMode: FolderMode? = null
        private set

    fun getFolders(): LiveData<List<DisplayFolder>> {
        return foldersFlow.asLiveData()
    }

    fun setDisplayMode(account: Account, displayMode: FolderMode) {
        currentDisplayMode = displayMode
        viewModelScope.launch {
            inputFlow.emit(DisplayMode(account, displayMode))
        }
    }
}

private data class DisplayMode(val account: Account, val displayMode: FolderMode)
