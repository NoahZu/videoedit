package io.github.noahzu.videoedit
import com.afollestad.materialdialogs.MaterialDialog
import com.haoqi.teacher.videoedit.data.VideoEditModel
import com.haoqi.teacher.videoedit.manager.VideoEditControlManager
import com.haoqi.teacher.videoedit.manager.VideoMenuControlManager
import com.haoqi.teacher.videoedit.manager.VideoPlayControlManager
import com.haoqi.teacher.videoedit.menuview.*
import com.haoqi.teacher.videoedit.widget.VideoEditTitleView
import io.github.noahzu.videoedit.base.ProgressLoadingDialog
import io.github.noahzu.videoedit.base.ToastUtils
import io.github.noahzu.videoedit.ext.beGone
import io.github.noahzu.videoedit.ext.beVisible
import io.github.noahzu.videoedit.menuview.SortVideoEditMenuPanel
import io.github.noahzu.videoedit.utils.FileUtils
import io.github.noahzu.videoedit.utils.VideoUtil
import kotlinx.android.synthetic.main.activity_video_edit.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

class VideoEditActivity : BaseActivity(), VideoMenuControlManager, VideoEditTitleView.ClickEventInterceptor {

    companion object {
        const val VIDEO_PATH = "video_path"
    }

    private  var videoPlayControlManager: VideoPlayControlManager?=null
    private lateinit var videoMenuControlManager: VideoMenuControlManager
    private lateinit var videoEditControlManger: VideoEditControlManager
    private lateinit var videoModel: VideoEditModel
    //all menus
    private lateinit var cutEditMenuPanel: EditMenuPanel
    private lateinit var markerEditMenuPanel: MarkerEditMenuPanel
    private lateinit var sortEditMenuPanel: SortVideoEditMenuPanel
    private lateinit var titleInputEditMenuPanel: TitleInputEditMenuPanel
    private lateinit var splitEditMenuPanel: SplitEditMenuPanel

    private lateinit var frontEditMenuPanel: EditMenuPanel
    private val progressDialog by lazy {
        ProgressLoadingDialog.Builder(this).setCancelOutside(false).setCancelable(false).setMessage("合成请勿退出").create()
    }

    override fun layoutId(): Int = R.layout.activity_video_edit

    override fun initialize() {
        loadData()
    }

    override fun onPause() {
        super.onPause()
        videoPlayerView.onPause()
    }

    override fun onResume() {
        super.onResume()
        videoPlayerView.onResume()
    }

    private fun loadData() {
        val videoPaths = intent.getStringArrayListExtra(VIDEO_PATH)
        videoModel = VideoEditModel()
        showProgress()
        GlobalScope.launch(Dispatchers.IO) {
            videoModel.loadData(videoPaths!!.toList())

            withContext(Dispatchers.Main) {
                if (videoModel.videoInfos.isEmpty()) {
                    ToastUtils.showToast("读取视频信息出错")
                    exitPageDirect()
                    return@withContext
                }
                onDataReady()
            }
        }
    }

    private fun onDataReady() {
        hideProgress()
        initManager()
        initActionMenus()
        initListener()
    }

    private fun initManager() {
        videoPlayControlManager = VideoPlayControlManager(findViewById(android.R.id.content), videoModel)
        videoMenuControlManager = this
        videoEditControlManger = VideoEditControlManager(videoModel)
    }

    private fun initActionMenus() {
        frontEditMenuPanel = mainEditMenuView
        cutEditMenuPanel = CutEditMenuPanel(this)
        markerEditMenuPanel = MarkerEditMenuPanel(this)
        sortEditMenuPanel = SortVideoEditMenuPanel(this)
        titleInputEditMenuPanel = TitleInputEditMenuPanel(this)
        splitEditMenuPanel = SplitEditMenuPanel(this)


        val menuList = ArrayList<EditMenuPanel>()
        menuList.add(mainEditMenuView)
        menuList.add(cutEditMenuPanel)
        menuList.add(markerEditMenuPanel)
        menuList.add(sortEditMenuPanel)
        menuList.add(titleInputEditMenuPanel)
        menuList.add(splitEditMenuPanel)

        menuList.forEach {
            it.initEditContext(
                videoModel = videoModel,
                titleView = titleBar,
                menuArea = if (it is SortVideoEditMenuPanel || it is TitleInputEditMenuPanel) {
                    fullscreenMenuArea
                } else {
                    actionMenuArea
                },
                playControlManager = videoPlayControlManager!!,
                menuControlManager = videoMenuControlManager,
                editControlManager = videoEditControlManger
            )
        }
    }

    private fun initListener() {
        titleBar.addClickEventInterceptor(this)
    }

    override fun openCutPanel() {
        cutEditMenuPanel.startEditAction()
        mainEditMenuView.beGone()
        frontEditMenuPanel = cutEditMenuPanel
    }

    override fun openMarkerPanel() {
        markerEditMenuPanel.startEditAction()
        mainEditMenuView.beGone()
        frontEditMenuPanel = markerEditMenuPanel
    }

    override fun openSortPanel() {
        sortEditMenuPanel.startEditAction()
        mainEditMenuView.beGone()
        frontEditMenuPanel = sortEditMenuPanel
    }

    override fun openTitleInputPanel() {
        titleInputEditMenuPanel.startEditAction()
        frontEditMenuPanel = titleInputEditMenuPanel
    }

    override fun openSplitPanel() {
        splitEditMenuPanel.startEditAction()
        mainEditMenuView.beGone()
        frontEditMenuPanel = splitEditMenuPanel
    }

    override fun onEndCut(isCancel: Boolean, isAllRefresh: Boolean) {
        mainEditMenuView.beVisible()
        if (!isCancel) {
            if (isAllRefresh) {
                mainEditMenuView.refreshVideoList()
            } else {
                mainEditMenuView.updateCurrentVideo()
            }
        } else {
            videoPlayControlManager?.run {
                play(currentIndex, playOption, null)
            }
        }
        frontEditMenuPanel = mainEditMenuView
    }

    override fun onEndMarker() {
        mainEditMenuView.beVisible()
        frontEditMenuPanel = mainEditMenuView
    }

    override fun onEndSort() {
        mainEditMenuView.beVisible()
        frontEditMenuPanel = mainEditMenuView
        mainEditMenuView.refreshVideoList()
    }

    override fun onEndTitleInput(isCancel: Boolean, file: File?) {
        mainEditMenuView.beVisible()
        if (!isCancel && file != null) {
            mainEditMenuView.addVideo(file)
            mainEditMenuView.refreshVideoList()
        }
        frontEditMenuPanel = mainEditMenuView

    }

    override fun onEndSplitPanel(isCancel: Boolean, endAction: VideoMenuControlManager.Action) {
        mainEditMenuView.beVisible()
        if (!isCancel) {
            mainEditMenuView.refreshVideoList()
        }
        frontEditMenuPanel = mainEditMenuView
        when (endAction) {
            VideoMenuControlManager.Action.ACTION_INSERT_TITLE -> {
                mainEditMenuView.insertTitle()
            }
            VideoMenuControlManager.Action.ACTION_INSERT_VIDEO -> {
                mainEditMenuView.insertVideo()
            }
            else -> {
            }
        }
    }

    override fun exitPage() {
        exitEditor()
    }

    override fun onClickEventIntercept(viewId: Int): Boolean {
        when (viewId) {
            R.id.titleBackBtn -> {
                exitEditor()
            }
            R.id.titleRightBtn -> {
                videoPlayControlManager?.pauseVideo()
                progressDialog.show()
                videoEditControlManger.startCompose(object : VideoUtil.EditCallback {
                    override fun onProgress(progress: Float) {
                        progressDialog.setProgress(progress)
                    }

                    override fun onFail(error: String) {
                        progressDialog.dismiss()
                        ToastUtils.showToast("合成失败")
                    }

                    override fun onSuccess(file: File?) {
                        videoModel.outputVideoFile = file
                        progressDialog.dismiss()
                        showShareAndSave()
                    }

                })
            }
        }
        return true
    }

    private fun showShareAndSave() {
        saveToAlbum()
        //
    }

    private fun retrieveCoverImage() {
        videoEditControlManger.retrieveCover(this)
    }

    private fun saveToAlbum() {
        videoModel.outputVideoFile?.let {
            FileUtils.saveVideoToGallery(this, it)?.let { saved ->
                videoModel.outputVideoFile = saved
                ToastUtils.showToast("文件保存到了${saved.path}")
            } ?: kotlin.run {
                ToastUtils.showToast("文件损坏，无法保存!")
            }

        }
    }

    private fun exitEditor() {
        showAskExitDialog()
    }

    override fun exitPageDirect() {
        finish()
    }

    private fun showAskExitDialog() {
        val dialog = MaterialDialog(this)
        dialog.show {
            title(text = "是否要放弃当前编辑?")
            positiveButton(R.string.cancel_edit) {
                finish()
            }
            negativeButton(R.string.continue_edit) {}
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        titleBar.removeClickEventInterceptor(this)
        videoPlayControlManager?.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (frontEditMenuPanel == mainEditMenuView) {
            exitEditor()
            return
        }
        frontEditMenuPanel.onBackPress()
    }

    fun showProgress(){

    }

    fun hideProgress() {

    }

}