package com.nwld.defi.tools.manager;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.nwld.defi.tools.MyApp;
import com.nwld.defi.tools.util.AESUtil;
import com.nwld.defi.tools.util.SPUtil;
import com.nwld.defi.tools.util.SecurityUtil;
import com.nwld.defi.tools.util.StringUtil;

import org.web3j.crypto.Credentials;

public class KeyManager {
    private String privateKey;
    private String pwd;

    private final MutableLiveData<Integer> keyData = new MutableLiveData<>();

    private static class ManagerHolder {
        private static final KeyManager manager = new KeyManager();
    }

    public static KeyManager getInstance() {
        return ManagerHolder.manager;
    }

    private KeyManager() {

    }

    public void watchKeyData(LifecycleOwner lifecycleOwner, Observer<Integer> observer) {
        if (null == lifecycleOwner || null == observer) {
            return;
        }
        keyData.observe(lifecycleOwner, observer);
    }

    public void unWatchKeyData(Observer<Integer> observer) {
        if (null == observer) {
            return;
        }
        keyData.removeObserver(observer);
    }

    public void setKeyData(int key) {
        keyData.postValue(key);
    }

    private String appKey() {
        return SecurityUtil.keccak256("!$VLk097+}MGfa?><B kws~zn587-fclhzflz9rqahgdz@&)+vznljhfao");
    }

    public void savePrivateKey(String priKey) throws Exception {
        this.privateKey = priKey;
        String enPrivateKey = AESUtil.encryptByHex(getPwd(), SecurityUtil.toHex(privateKey));
        SPUtil.set(MyApp.getContext(), "key", "priKey", enPrivateKey);
    }

    private String getPrivateKey() {
        if (!StringUtil.isEmpty(privateKey)) {
            return privateKey;
        }
        privateKey = getSavePriKey();
        return privateKey;
    }

    public String getSavePriKey() {
        String enPrivateKey = SPUtil.get(MyApp.getContext(), "key", "priKey");
        if (StringUtil.isEmpty(enPrivateKey)) {
            return null;
        }
        return SecurityUtil.fromHex(AESUtil.decryptByHex(getPwd(), enPrivateKey));
    }

    private String getPwd() {
        if (!StringUtil.isEmpty(pwd)) {
            return pwd;
        }
        String enPwdHash = SPUtil.get(MyApp.getContext(), "key", "pwd");
        if (!StringUtil.isEmpty(enPwdHash)) {
            pwd = AESUtil.decryptByHex(appKey(), enPwdHash);
            return pwd;
        }
        String password = SecurityUtil.getRandomPwd();
        String pwdHash = SecurityUtil.keccak256(SecurityUtil.keccak256(password));
        pwd = pwdHash;
        enPwdHash = AESUtil.encryptByHex(appKey(), pwdHash);
        SPUtil.set(MyApp.getContext(), "key", "pwd", enPwdHash);
        return pwd;
    }

    public Credentials getCredentials() {
        String privateKey = getPrivateKey();
        if (StringUtil.isEmpty(privateKey)) {
            return null;
        }
        return Credentials.create(privateKey);
    }
}
