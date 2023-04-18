package com.shine.mglm.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.shine.mglm.DaoManager;
import com.shine.mglm.UserBean;
import com.shine.mglm.db.greendao.UserBeanDao;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.List;

public class DbOperationUtils {

    private static final String TAG = DbOperationUtils.class.getSimpleName();
    private DaoManager mManager;

    public DbOperationUtils(Context context) {
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }

    /**
     * 完成UserBean记录的插入，如果表未创建，先创建UserBean表
     *
     * @param user
     * @return
     */
    public boolean insertuser(UserBean user) {
        boolean flag = false;
        flag = mManager.getDaoSession().getUserBeanDao().insert(user) == -1 ? false : true;
        Log.i(TAG, "insert user :" + flag + "-->" + user.toString());
        return flag;
    }

    /**
     * 插入多条数据，在子线程操作
     *
     * @param userList
     * @return
     */
    public boolean insertMultuser(final List<UserBean> userList) {
        boolean flag = false;
        try {
            mManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (UserBean user : userList) {
                        mManager.getDaoSession().insertOrReplace(user);
                    }
                }
            });
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 修改一条数据
     *
     * @param user
     * @return
     */
    public boolean updateuser(UserBean user) {
        boolean flag = false;
        try {
            mManager.getDaoSession().update(user);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除单条记录
     *
     * @param user
     * @return
     */
    public boolean deleteuser(UserBean user) {
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().delete(user);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除所有记录
     *
     * @return
     */
    public boolean deleteAll() {
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().deleteAll(UserBean.class);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 查询所有记录
     *
     * @return
     */
    public List<UserBean> queryAlluser() {
        return mManager.getDaoSession().loadAll(UserBean.class);
    }

    /**
     * 根据主键id查询记录
     *
     * @param key
     * @return
     */
    public UserBean queryuserById(long key) {
        return mManager.getDaoSession().load(UserBean.class, key);
    }

    /**
     * 使用native sql进行查询操作
     */
    public List<UserBean> queryuserByNativeSql(String sql, String[] conditions) {
        return mManager.getDaoSession().queryRaw(UserBean.class, sql, conditions);
    }

    /**
     * 使用queryBuilder进行查询
     *
     * @return
     */
    public List<UserBean> queryuserByQueryBuilder(long id) {
        QueryBuilder<UserBean> queryBuilder = mManager.getDaoSession().queryBuilder(UserBean.class);
        return queryBuilder.where(UserBeanDao.Properties.UserId.eq(id)).list();
    }

    public List<UserBean> queryLikeUser(int page, int size) {
        QueryBuilder<UserBean> queryBuilder = mManager.getDaoSession().queryBuilder(UserBean.class);
        return queryBuilder
                .orderDesc(UserBeanDao.Properties.UserId)
//                .offset(page - 1)
                .limit(page * size)
                .list();
    }

    public List<UserBean> queryLikeUser(String phone) {
        QueryBuilder<UserBean> queryBuilder = mManager.getDaoSession().queryBuilder(UserBean.class);
        return queryBuilder
                .where(UserBeanDao.Properties.Phone.like("%" + phone + "%"))
                .list();
    }


}
