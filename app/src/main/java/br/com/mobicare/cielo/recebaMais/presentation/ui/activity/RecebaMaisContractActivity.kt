package br.com.mobicare.cielo.recebaMais.presentation.ui.activity

import android.Manifest
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.view.View
import androidx.core.app.ActivityCompat
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.shockwave.pdfium.PdfDocument
import kotlinx.android.synthetic.main.activity_contract_receba_mais.*
import kotlinx.android.synthetic.main.toolbar_dialog.*
import java.io.File

class RecebaMaisContractActivity : BaseLoggedActivity(), OnPageChangeListener, OnLoadCompleteListener,
        OnPageErrorListener {

    private lateinit var broadcastReceiver: BroadcastReceiver
    private lateinit var downloadmanager: DownloadManager
    private var enqueue: Long = 0
    private var pdfFileName: String? = null
    private var pageNumber = 0
    private val filePdf = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "CIELO_CCB_Site_App.pdf")
    private val urlDownload = "https://apollo-receba-mais.s3-sa-east-1.amazonaws.com/CIELO_CCB_Site_App.pdf"

    private val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 111
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contract_receba_mais)


        configureReciberDocument()
        configureButtonToobar()
        showPdf()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }

    private fun showPdf() {
        callWhenHasPermission()
    }

    private fun callWhenHasPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                setupPermissionInfoDialog()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE)
            }
        } else {
            downloadFile(urlDownload, filePdf)
        }
    }

    private fun setupPermissionInfoDialog() {
        showOptionDialogMessage {
            title = getString(R.string.rm_title_message)
            setBtnLeft("Não")
            setBtnRight("Sim")
            setMessage("Para ler o contrato é necessário a permissão para leitura e gravação de arquivos." +
                    "\nDeseja conceder esse acesso?")
            setOnclickListenerRight {
                ActivityCompat
                        .requestPermissions(this@RecebaMaisContractActivity,
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE)
            }
            setOnclickListenerLeft {
                onBackPressed()
            }
        }
    }

    private fun configureButtonToobar() {
        btnLeft.visibility = View.GONE
        txtTitle.text = getString(R.string.receba_mais_contrato_text_title)
        btnRight.setOnClickListener {
            onBackPressed()
        }
    }

    private fun configureReciberDocument() {
        broadcastReceiver = object : BroadcastReceiver() {

            override fun onReceive(context: Context?, intent: Intent?) {
                val action = intent?.getAction();
                action.let {
                    if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(it)) {
                        val downloadId = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)
                        downloadId.let {
                            val query = DownloadManager.Query()
                            query.setFilterById(enqueue)
                            val c = downloadmanager.query(query)
                            if (c.moveToFirst()) {
                                val columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                                if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {

                                    //ImageView view = (ImageView) findViewById(R.id.imageView1);
                                    val uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                                    displayFromUri(Uri.parse(uriString))
                                }
                            }
                        }
                    }

                }
            }
        }

        registerReceiver(broadcastReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    downloadFile(urlDownload, filePdf)
                } else {
                    onBackPressed()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun downloadFile(_url: String, _name: File) {
        if (isAttached()) {
            progress_main.visibility = View.VISIBLE
            downloadmanager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val uri = Uri.parse(_url)

            val request = DownloadManager.Request(uri)
            request.setTitle(getString(R.string.receba_mais_contrato_text_title))
            request.setDescription("Downloading")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setVisibleInDownloadsUi(false)
            request.setDestinationUri(Uri.fromFile(_name))

            enqueue = downloadmanager.enqueue(request)
        }
    }

    //region PdfView

    private fun displayFromUri(uri: Uri) {
        pdfFileName = getFileName(uri)

        pdfView.fromUri(uri)
                .defaultPage(pageNumber)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(DefaultScrollHandle(this))
                .spacing(10) // in dp
                .onPageError(this)
                .load()

        progress_main.visibility = View.GONE
    }

    fun getFileName(uri: Uri): String? {
        var result: String? = uri.lastPathSegment
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        return result
    }

    //region PdfViewListener
    override fun onPageChanged(page: Int, pageCount: Int) {
        pageNumber = page
        title = String.format("%s %s / %s", pdfFileName, page + 1, pageCount)
    }

    override fun loadComplete(nbPages: Int) {
        printBookmarksTree(pdfView.tableOfContents, "-")
    }

    fun printBookmarksTree(tree: List<PdfDocument.Bookmark>, sep: String) {
        for (b in tree) {

            if (b.hasChildren()) {
                printBookmarksTree(b.children, "$sep-")
            }
        }
    }

    override fun onPageError(page: Int, t: Throwable?) {
    }
    //endregion
    //endregion

}