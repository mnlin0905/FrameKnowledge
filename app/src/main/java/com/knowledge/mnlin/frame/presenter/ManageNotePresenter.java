package com.knowledge.mnlin.frame.presenter;


import com.knowledge.mnlin.frame.base.BasePresenter;
import com.knowledge.mnlin.frame.contract.ManageNoteContract;
import com.knowledge.mnlin.frame.activity.ManageNoteActivity;

import javax.inject.Inject;

public class ManageNotePresenter extends BasePresenter<ManageNoteActivity> implements ManageNoteContract.Presenter{
    @Inject
    public ManageNotePresenter() {}

}