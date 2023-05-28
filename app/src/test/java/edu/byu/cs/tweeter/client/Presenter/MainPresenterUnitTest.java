package edu.byu.cs.tweeter.client.Presenter;

import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.Handler.PostStatusHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenterUnitTest {
    private MainPresenter.MainView mockView;
    private StatusService mockStatusService;
    private Cache mockCache;
    private User mockUser;
    private Status mockStatus;
    private AuthToken mockAuthToken;
    private MainPresenter mainPresenterSpy;

    @BeforeEach
    public void setup(){
        //Create mocks
        mockView = Mockito.mock(MainPresenter.MainView.class);
        mockStatusService = Mockito.mock(StatusService.class);
        mockCache = Mockito.mock(Cache.class);
        mockUser = Mockito.mock(User.class);
        mockStatus = Mockito.mock(Status.class);
        mockAuthToken = Mockito.mock(AuthToken.class);

        mainPresenterSpy = Mockito.spy(new MainPresenter(mockView, mockUser, mockAuthToken));
        Mockito.doReturn(mockStatusService).when(mainPresenterSpy).getStatusService();

        Cache.setInstance(mockCache);
    }

    @Test
    public void testPostStatus_postStatusSuccessful(){
        String testPost = "Test String to Post";

        Mockito.doAnswer(new Answer<Void>() {
            @Override public Void answer(InvocationOnMock invocation) {
                MainPresenter.ConcretePostStatusObserver observer = invocation.getArgument(2, MainPresenter.ConcretePostStatusObserver.class);
                observer.handleSuccess();
                return null;
            }
        }).when(mockStatusService).postStatus(Mockito.any(AuthToken.class), Mockito.any(Status.class), Mockito.any(MainPresenter.ConcretePostStatusObserver.class));

        mainPresenterSpy.postStatus(mockAuthToken, testPost);
        Mockito.verify(mockView).postStatusSuccessful();
        Mockito.verify(mockView).displayInfoMessage("Successfully Posted!");
    }

    @Test
    public void testPostStatus_postStatusSuccessfulWithCorrectParams(){
        String testPost = "Test String to Post";

        Mockito.doAnswer(new Answer<Void>() {
            @Override public Void answer(InvocationOnMock invocation) {
                MainPresenter.ConcretePostStatusObserver observer = invocation.getArgument(2, MainPresenter.ConcretePostStatusObserver.class);
                Status testStatus = invocation.getArgument(1, Status.class);
                Assertions.assertEquals(testPost, testStatus.getPost());
                observer.handleSuccess();
                return null;
            }
        }).when(mockStatusService).postStatus(Mockito.any(AuthToken.class), Mockito.any(Status.class), Mockito.any(MainPresenter.ConcretePostStatusObserver.class));

        mainPresenterSpy.postStatus(mockAuthToken, testPost);
        Mockito.verify(mockStatusService).postStatus(Mockito.any(AuthToken.class), Mockito.any(Status.class), Mockito.any(MainPresenter.ConcretePostStatusObserver.class));
        Mockito.verify(mockView).postStatusSuccessful();
        Mockito.verify(mockView).displayInfoMessage("Successfully Posted!");
    }

    @Test
    public void testPostStatus_postStatusFailedWithMessage(){
        String testPost = "Test String to Post";

        Mockito.doAnswer(new Answer<Void>() {
            @Override public Void answer(InvocationOnMock invocation) {
                MainPresenter.ConcretePostStatusObserver observer = invocation.getArgument(2, MainPresenter.ConcretePostStatusObserver.class);
                observer.handleFailure("error message");
                return null;
            }
        }).when(mockStatusService).postStatus(Mockito.any(AuthToken.class), Mockito.any(Status.class), Mockito.any(MainPresenter.ConcretePostStatusObserver.class));

        mainPresenterSpy.postStatus(mockAuthToken, testPost);
        Mockito.verify(mockView).displayInfoMessage("error message");

    }

    @Test
    public void testPostStatus_postStatusFailedWithException(){
        String testPost = "Test String to Post";

        Mockito.doAnswer(new Answer<Void>() {
            @Override public Void answer(InvocationOnMock invocation) {
                MainPresenter.ConcretePostStatusObserver observer = invocation.getArgument(2, MainPresenter.ConcretePostStatusObserver.class);
                observer.handleException(new Exception("exception message"));
                return null;
            }
        }).when(mockStatusService).postStatus(Mockito.any(AuthToken.class), Mockito.any(Status.class), Mockito.any(MainPresenter.ConcretePostStatusObserver.class));

        mainPresenterSpy.postStatus(mockAuthToken, testPost);
        Mockito.verify(mockView).displayInfoMessage("exception message");
    }
}
