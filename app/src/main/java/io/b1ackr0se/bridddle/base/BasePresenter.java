package io.b1ackr0se.bridddle.base;

public class BasePresenter<T extends BaseView> implements Presenter<T> {
    private T view;
    @Override
    public void attachView(T view) {
        this.view = view;
    }
    @Override
    public void detachView() {
        this.view = null;
    }
    public T getView() {
        return view;
    }
    public boolean isViewAttached() {
        return view != null;
    }
    public void checkViewAttached() {
        if (!isViewAttached()) throw new ViewNotAttachedException();
    }
}