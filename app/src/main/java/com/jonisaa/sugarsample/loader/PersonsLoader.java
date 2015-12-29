package com.jonisaa.sugarsample.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.jonisaa.sugarsample.component.DaggerControllerComponent;
import com.jonisaa.sugarsample.controller.base.Controller;
import com.jonisaa.sugarsample.model.Person;

import java.util.List;

/**
 * @author jonatan.salas
 */
public class PersonsLoader extends AsyncTaskLoader<List<Person>> {
    private static final String LOG_TAG = PersonsLoader.class.getSimpleName();
    private Controller<Person> mPersonController;
    private final Object mLock = new Object();

    public PersonsLoader(Context context) {
        super(context);
        this.mPersonController = DaggerControllerComponent.create().providePersonController();
    }

    @Override
    public List<Person> loadInBackground() {
        List<Person> personList = null;

        if (mPersonController.getCount(Person.class) == 0) {
            try {
                synchronized (mLock) {
                    mLock.wait(3000);
                }
            } catch (InterruptedException ex) {
                Log.e(LOG_TAG, ex.getMessage(), ex.getCause());
            }
        } else {
            synchronized (mLock) {
                personList = mPersonController.listAll(Person.class);
                mLock.notify();
            }
        }

        return (null != personList) ? personList : loadInBackground();
    }
}
