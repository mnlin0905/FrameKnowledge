package com.knowledge.mnlin.frame.presenter;


import com.knowledge.mnlin.frame.base.BasePresenter;
import com.knowledge.mnlin.frame.contract.EditNoteContract;
import com.knowledge.mnlin.frame.activity.EditNoteActivity;

import javax.inject.Inject;

public class EditNotePresenter extends BasePresenter<EditNoteActivity> implements EditNoteContract.Presenter{
    @Inject
    public EditNotePresenter() {}

}