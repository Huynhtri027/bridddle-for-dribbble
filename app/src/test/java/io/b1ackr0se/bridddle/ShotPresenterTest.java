package io.b1ackr0se.bridddle;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import io.b1ackr0se.bridddle.data.model.Comment;
import io.b1ackr0se.bridddle.data.model.Shot;
import io.b1ackr0se.bridddle.data.remote.dribbble.DataManager;
import io.b1ackr0se.bridddle.test.common.MockModel;
import io.b1ackr0se.bridddle.test.common.RxSchedulersOverrideRule;
import io.b1ackr0se.bridddle.ui.detail.ShotPresenter;
import io.b1ackr0se.bridddle.ui.detail.ShotView;
import rx.Observable;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class ShotPresenterTest {

    @Mock ShotView mockView;
    @Mock DataManager mockDataManager;

    private ShotPresenter presenter;

    @Rule
    public final RxSchedulersOverrideRule overrideSchedulersRule = new RxSchedulersOverrideRule();

    @Before
    public void setUp() {
        presenter = new ShotPresenter(mockDataManager);
        presenter.attachView(mockView);

    }

    @After
    public void detachView() {
        presenter.detachView();
    }

    @Test
    public void testViewBind() {

        presenter.load();

        verify(mockView).bind();

    }

    @Test
    public void testGetCommentsSuccessful() {
        List<Comment> comments = MockModel.newCommentList(40);

        dataManagerReturnFakeComment(Observable.just(comments));

        verify(mockView).showComments(comments);
    }

    @Test
    public void testGetCommentsFailed() {

        dataManagerReturnFakeComment(Observable.error(new RuntimeException()));

        verify(mockView).showError();
    }

    @Test
    public void testGetNoComment() {

        List<Comment> comments = new ArrayList<>();

        dataManagerReturnFakeComment(Observable.just(comments));

        verify(mockView).showNoComment();
    }

    @Test
    public void showUnlikeWhenNotLoggedIn() {
        Shot shot = MockModel.newShot();
        presenter.setShot(shot);

        dataManagerLoginStatus(false);

        presenter.checkLike();

        verify(mockView).showLike(false);
    }

    @Test
    public void showUnlikeWhenShotIsNull() {
        dataManagerLoginStatus(false);

        presenter.checkLike();

        verify(mockView).showLike(false);
    }

    @Test
    public void testLikeWhenNotLoggedIn() {
        Shot shot = MockModel.newShot();
        presenter.setShot(shot);

        dataManagerLoginStatus(false);

        presenter.like();

        verify(mockView).showLike(false);
        verify(mockView).performLogin();
    }

    @Test
    public void testLikeWhenLoggedIn() {
        Shot shot = MockModel.newShot();
        presenter.setShot(shot);

        dataManagerLoginStatus(true);

        dataManagerReturnLike(shot.getId());

        presenter.like();

        verify(mockView).showLike(true);
    }

    @Test
    public void testUnlikeWhenNotLoggedIn() {
        Shot shot = MockModel.newShot();
        presenter.setShot(shot);

        dataManagerLoginStatus(false);

        presenter.unlike();

        verify(mockView).showLike(false);
        verify(mockView).performLogin();
    }

    @Test
    public void testUnlikeWhenLoggedIn() {
        Shot shot = MockModel.newShot();
        presenter.setShot(shot);

        dataManagerLoginStatus(true);

        doReturn(Observable.empty())
                .when(mockDataManager)
                .unlike(shot.getId());

        presenter.unlike();

        verify(mockView).showLike(false);
    }


    public void dataManagerReturnFakeComment(Observable observable) {
        Shot shot = MockModel.newShot();
        presenter.setShot(shot);

        doReturn(observable)
                .when(mockDataManager)
                .getComments(shot.getId(), 1, 40);

        presenter.loadComment(true);
    }

    public void dataManagerLoginStatus(boolean isLoggedIn) {
        doReturn(isLoggedIn)
                .when(mockDataManager)
                .isLoggedIn();
    }

    public void dataManagerReturnLike(int id) {
        doReturn(Observable.just(MockModel.newLike()))
                .when(mockDataManager)
                .like(id);
    }

}
