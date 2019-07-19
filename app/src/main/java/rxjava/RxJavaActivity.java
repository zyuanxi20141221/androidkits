package rxjava;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import zheng.androidkits.R;

/**
 * Created by zheng on 2019/7/11
 */
public class RxJavaActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rxjava);

        /*方式一：执行和回调都在主线程，没有链式编程
        Observable observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter observableEmitter) throws Exception {

                int i = 0;
                while (true) {
                    Thread.sleep(500);
                    i++;
                    observableEmitter.onNext("连载" + i);
                    if (i >= 100) {
                        break;
                    }
                }
                observableEmitter.onComplete();
            }
        });

        Observer observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                Log.e("RxJavaActivity", "onSubscribe");
            }

            @Override
            public void onNext(String value) {
                Log.e("RxJavaActivity", "onNext " + value);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("RxJavaActivity", "onError");
            }

            @Override
            public void onComplete() {
                Log.e("RxJavaActivity", "onComplete");
            }
        };
        observable.subscribe(observer);
        */

        /* 方式二，异步+链式编程
        Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter observableEmitter) throws Exception {
                int i = 0;
                while (true) {
                    Thread.sleep(500);
                    i++;
                    observableEmitter.onNext("连载" + i);
                    if (i >= 100) {
                        break;
                    }
                }
                observableEmitter.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread()) //回调在主线程
                .subscribeOn(Schedulers.io()) //执行在子线程
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        Log.e("RxJavaActivity", "onSubscribe");
                    }

                    @Override
                    public void onNext(String value) {
                        Log.e("RxJavaActivity", "onNext " + value);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("RxJavaActivity", "onError");
                    }

                    @Override
                    public void onComplete() {
                        Log.e("RxJavaActivity", "onComplete");
                    }
                });
                */

        Observable.just(1,2,3)
                .subscribe(new Observer<Integer>() {

                    @Override
                    public void onSubscribe(Disposable disposable) {
                        Log.e("RxJavaActivity", "onSubscribe");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e("RxJavaActivity", "onNext " + integer);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("RxJavaActivity", "onError");
                    }

                    @Override
                    public void onComplete() {
                        Log.e("RxJavaActivity", "onComplete");
                    }
                });

        Observable.fromArray(new String[] {"aaa", "bbb", "ccc", "ddd", "eee"})
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        Log.e("RxJavaActivity", "onSubscribe");
                    }

                    @Override
                    public void onNext(String s) {
                        Log.e("RxJavaActivity", "onNext " + s);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("RxJavaActivity", "onError");
                    }

                    @Override
                    public void onComplete() {
                        Log.e("RxJavaActivity", "onComplete");
                    }
                });


        Observable.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return 1;
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.e("RxJavaActivity", "accept " + integer);
            }
        });

        Observable.timer(2, TimeUnit.SECONDS)
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        Log.e("RxJavaActivity", "onSubscribe");
                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.e("RxJavaActivity", "onNext " + aLong);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("RxJavaActivity", "onError");
                    }

                    @Override
                    public void onComplete() {
                        Log.e("RxJavaActivity", "onComplete");
                    }
                });

        Observable.interval(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.e("RxJavaActivity", "onNext " + aLong);
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
