package com.sairic.idea.plugins;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.CommitMessageI;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.VcsDataKeys;
import com.intellij.openapi.vcs.impl.ProjectLevelVcsManagerImpl;
import com.intellij.openapi.vcs.ui.Refreshable;
import git4idea.GitLocalBranch;
import git4idea.branch.GitBranchUtil;

import org.jetbrains.annotations.Nullable;



public class StoryPrependPlugin extends AnAction implements DumbAware {

    
    @Override
    public void actionPerformed(AnActionEvent e) {
        final CommitMessageI checkinPanel = getCheckinPanel(e);
        if (checkinPanel == null)
            return;

        String commitMessage = extractBranchName(e.getProject());
        if (!commitMessage.isEmpty()) {
            checkinPanel.setCommitMessage(commitMessage);
        }
    }

   

    @Nullable
    private static CommitMessageI getCheckinPanel(@Nullable AnActionEvent e) {
        if (e == null) {
            return null;
        }
        Refreshable data = Refreshable.PANEL_KEY.getData(e.getDataContext());
        if (data instanceof CommitMessageI) {
            return (CommitMessageI) data;
        }
        CommitMessageI commitMessageI = VcsDataKeys.COMMIT_MESSAGE_CONTROL.getData(e.getDataContext());
        if (commitMessageI != null) {
            return commitMessageI;
        }
        return null;
    }

    private String extractBranchName(Project project) {
        String branch = "";
        ProjectLevelVcsManager instance = ProjectLevelVcsManagerImpl.getInstance(project);
        if (instance.checkVcsIsActive("Git")) {
            GitLocalBranch currentBranch = GitBranchUtil.getCurrentRepository(project).getCurrentBranch();

            if (currentBranch != null) {
                // Branch name  matches Ticket Name
                branch = currentBranch.getName().trim();
                int index = branch.indexOf('/');
                if(index != -1) {
                    branch = branch.substring(index + 1);
                }
                branch = branch.toUpperCase();
            }
        } 

        return branch;
    }
}