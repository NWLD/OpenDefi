package com.nwld.defi.tools.util;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

public class FragmentUtil {
    private static void addFragment(FragmentActivity fragmentActivity, FragmentManager fragmentManager,
                                    int resId, Fragment fragment) {
        if (fragmentActivity == null || fragment == null
                || fragmentActivity.isDestroyed() || null == fragmentManager) {
            return;
        }
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        fragmentTransaction.add(resId, fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }


    /**
     * @Title:addFragment
     * @Description:添加一个Fragment
     * @param:@param fragmentManager： Fragment管理器
     * @param:@param resId： 添加Fragment的布局id
     * @param:@param fragment： 添加的Fragment
     * @return:void
     * @throws:
     */
    public static void addFragment(FragmentActivity fragmentActivity,
                                   int resId, Fragment fragment) {
        if (fragmentActivity == null
                || fragmentActivity.isDestroyed()) {
            return;
        }
        FragmentManager fragmentManager = fragmentActivity
                .getSupportFragmentManager();
        addFragment(fragmentActivity, fragmentManager, resId, fragment);
    }

    /**
     * @Title:removeFragment
     * @Description:移除一个Fragment
     * @param:@param fragmentManager： Fragment管理器
     * @param:@param fragment： 移除的Fragment
     * @return:void
     * @throws:
     */
    public static void removeFragment(FragmentActivity fragmentActivity,
                                      Fragment fragment) {
        if (fragmentActivity == null) {
            return;
        }
        FragmentManager fragmentManager = fragmentActivity
                .getSupportFragmentManager();
        removeFragment(fragmentActivity, fragmentManager, fragment);
    }

    private static void removeFragment(FragmentActivity fragmentActivity, FragmentManager fragmentManager,
                                       Fragment fragment) {
        if (fragmentActivity == null || fragment == null
                || fragmentActivity.isDestroyed() || null == fragmentManager) {
            return;
        }
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * 显示Fragment
     *
     * @param fragmentActivity
     * @param fragment
     */
    public static void showFragment(FragmentActivity fragmentActivity,
                                    Fragment fragment) {
        if (fragmentActivity == null || fragment == null) {
            return;
        }
        FragmentManager fragmentManager = fragmentActivity
                .getSupportFragmentManager();
        showFragment(fragmentActivity, fragmentManager, fragment);
    }

    private static void showFragment(FragmentActivity fragmentActivity, FragmentManager fragmentManager,
                                     Fragment fragment) {
        if (fragmentActivity == null || fragment == null
                || fragmentActivity.isDestroyed() || null == fragmentManager) {
            return;
        }
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        fragmentTransaction.show(fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * 隐藏Fragment
     *
     * @param fragmentActivity
     * @param fragment
     */
    public static void hideFragment(FragmentActivity fragmentActivity,
                                    Fragment fragment) {
        if (fragmentActivity == null || fragment == null) {
            return;
        }
        FragmentManager fragmentManager = fragmentActivity
                .getSupportFragmentManager();
        hideFragment(fragmentActivity, fragmentManager, fragment);
    }

    private static void hideFragment(FragmentActivity fragmentActivity, FragmentManager fragmentManager,
                                     Fragment fragment) {
        if (fragmentActivity == null || fragment == null
                || fragmentActivity.isDestroyed() || null == fragmentManager) {
            return;
        }
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        fragmentTransaction.hide(fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    public static void removeAllFragment(FragmentActivity fragmentActivity) {
        if (fragmentActivity == null) {
            return;
        }
        FragmentManager fragmentManager = fragmentActivity
                .getSupportFragmentManager();
        removeAllFragment(fragmentActivity, fragmentManager);
    }

    private static void removeAllFragment(FragmentActivity fragmentActivity, FragmentManager fragmentManager) {
        if (fragmentActivity == null
                || fragmentActivity.isDestroyed() || null == fragmentManager) {
            return;
        }
        if (fragmentManager != null) {
            List<Fragment> fragments = fragmentManager.getFragments();
            if (null != fragments && fragments.size() > 0) {
                FragmentTransaction fragmentTransaction = fragmentManager
                        .beginTransaction();
                for (int index = 0; index < fragments.size(); index++) {
                    fragmentTransaction.remove(fragments.get(index));
                }
                fragmentTransaction.commitAllowingStateLoss();
            }
        }
    }
}
