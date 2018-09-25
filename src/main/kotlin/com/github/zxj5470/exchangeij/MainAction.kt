package com.github.zxj5470.exchangeij

import com.intellij.notification.*
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange

object CopyData {
    var start: Int = 0
    var end: Int = 0
}

class ActionCopy : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val selectionModel = editor.selectionModel

        val start = selectionModel.selectionStart
        val end = selectionModel.selectionEnd
        CopyData.start = start
        CopyData.end = end
    }
}

class ActionV : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val project = e.project ?: return
        val document = editor.document
        val selectionModel = editor.selectionModel
        selectionModel.selectedText ?: return

        val originText = editor.document.getText(TextRange.from(CopyData.start, CopyData.end - CopyData.start))

        val start = selectionModel.selectionStart
        val end = selectionModel.selectionEnd
        val currentText = editor.document.getText(TextRange.from(start, end - start))
        WriteCommandAction.runWriteCommandAction(project) {
            CommandProcessor.getInstance().runUndoTransparentAction {
                if (CopyData.start < start && CopyData.end < end) {
                    document.replaceString(start, end, originText)
                    document.replaceString(CopyData.start, CopyData.end, currentText)
                } else if (CopyData.start > start && CopyData.end > end) {
                    document.replaceString(CopyData.start, CopyData.end, currentText)
                    document.replaceString(start, end, originText)
                } else {
                    errorNotification(project,"It seems that you make a invalid index.")
                }
            }
        }
    }
}

fun errorNotification(project: Project?, message: String) {
    val errorTag = "Exchange Index Exception"
    val errorTitle = "Invalid Index Exception"
    Notifications.Bus.notify(Notification(errorTag, errorTitle, message, NotificationType.ERROR), project)
}