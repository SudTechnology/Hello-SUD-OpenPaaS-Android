package tech.sud.mgp.hello.runtime2;

import android.content.Context;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import tech.sud.gip.r2.core.SUDRuntime2;
import tech.sud.gip.r2.core.SUDRuntime2GameCoreHandle;
import tech.sud.gip.r2.core.SUDRuntime2GameRuntime;


/** 用于初始化环境 */
public class SUDRuntime2InitManager {

    private static SUDRuntime2GameRuntime sGameRuntime;
    private static SUDRuntime2GameCoreHandle sGameCoreHandle;
    private static boolean isInitializingRuntime;
    private static boolean isInitializingCore;
    private static List<CreateRuntimeListener> sRuntimeListenerList;
    private static List<LoadCoreListener> sCoreListenerList;

    /** 初始化runtime */
    public static void createRuntime(Context context, CreateRuntimeListener listener) {
        if (listener == null) {
            return;
        }
        if (context == null) {
            listener.onFailure(new RuntimeException("context can not be empty"));
            return;
        }
        if (sGameRuntime != null) {
            listener.onSuccess(sGameRuntime);
            return;
        }
        synchronized (BaseRuntime2GameViewModel.class) {
            if (isInitializingRuntime) {
                if (sRuntimeListenerList == null) {
                    sRuntimeListenerList = new ArrayList<>();
                }
                sRuntimeListenerList.add(listener);
                return;
            }
            isInitializingRuntime = true;
            SUDRuntime2.createRuntime(context, null, new SUDRuntime2GameRuntime.RuntimeCreateListener() {
                @Override
                public void onSuccess(SUDRuntime2GameRuntime runtime) {
                    sGameRuntime = runtime;
                    listener.onSuccess(runtime);
                    notifyInitRuntimeOnSuccess();
                    isInitializingRuntime = false;
                }

                @Override
                public void onFailure(Throwable error) {
                    listener.onFailure(error);
                    notifyInitRuntimeOnFailure(error);
                    isInitializingRuntime = false;
                }
            });
        }
    }

    /** 初始化core */
    public static void loadCore(LoadCoreListener listener) {
        if (listener == null) {
            return;
        }
        if (sGameCoreHandle != null) {
            listener.onSuccess(sGameCoreHandle);
            return;
        }
        synchronized (BaseRuntime2GameViewModel.class) {
            if (isInitializingCore) {
                if (sCoreListenerList == null) {
                    sCoreListenerList = new ArrayList<>();
                }
                sCoreListenerList.add(listener);
                return;
            }
            isInitializingCore = true;
            long start = System.currentTimeMillis();
            // 不是动态core 不传 core 的 options
            sGameRuntime.loadCore(null, new SUDRuntime2GameRuntime.CoreLoadListener() {
                @Override
                public void onSuccess(SUDRuntime2GameCoreHandle coreHandle) {
                    sGameCoreHandle = coreHandle;
                    listener.onSuccess(coreHandle);
                    notifyInitCoreOnSuccess();
                    isInitializingCore = false;
                }

                @Override
                public void onFailure(Throwable error) {
                    listener.onFailure(error);
                    notifyInitCoreOnFailure(error);
                    isInitializingCore = false;
                }
            });
        }
    }

    private static void notifyInitRuntimeOnSuccess() {
        if (sRuntimeListenerList == null) {
            return;
        }
        Iterator<CreateRuntimeListener> iterator = sRuntimeListenerList.iterator();
        while (iterator.hasNext()) {
            CreateRuntimeListener next = iterator.next();
            next.onSuccess(sGameRuntime);
            iterator.remove();
        }
    }

    private static void notifyInitRuntimeOnFailure(Throwable error) {
        if (sRuntimeListenerList == null) {
            return;
        }
        Iterator<CreateRuntimeListener> iterator = sRuntimeListenerList.iterator();
        while (iterator.hasNext()) {
            CreateRuntimeListener next = iterator.next();
            next.onFailure(error);
            iterator.remove();
        }
    }

    private static void notifyInitCoreOnSuccess() {
        if (sCoreListenerList == null) {
            return;
        }
        Iterator<LoadCoreListener> iterator = sCoreListenerList.iterator();
        while (iterator.hasNext()) {
            LoadCoreListener next = iterator.next();
            next.onSuccess(sGameCoreHandle);
            iterator.remove();
        }
    }

    private static void notifyInitCoreOnFailure(Throwable error) {
        if (sCoreListenerList == null) {
            return;
        }
        Iterator<LoadCoreListener> iterator = sCoreListenerList.iterator();
        while (iterator.hasNext()) {
            LoadCoreListener next = iterator.next();
            next.onFailure(error);
            iterator.remove();
        }
    }

    public interface CreateRuntimeListener {
        void onSuccess(SUDRuntime2GameRuntime runtime);

        void onFailure(Throwable error);
    }

    public interface LoadCoreListener {
        void onSuccess(SUDRuntime2GameCoreHandle coreHandle);

        void onFailure(Throwable error);
    }

}
