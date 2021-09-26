package com.nwld.defi.tools.util;

import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.web3j.utils.Numeric;

import java.security.SecureRandom;

public class PriKeyUtil {
    public  static  String generateNewPrivateKey() {
        final ECKeyPairGenerator generator = new ECKeyPairGenerator();
        final X9ECParameters CURVE_PARAMS = CustomNamedCurves.getByName("secp256k1");
        final ECDomainParameters CURVE = new ECDomainParameters(
                CURVE_PARAMS.getCurve(), CURVE_PARAMS.getG(), CURVE_PARAMS.getN(), CURVE_PARAMS.getH());
        SecureRandom random = new SecureRandom();
        final ECKeyGenerationParameters keyParams = new ECKeyGenerationParameters(CURVE, random);
        generator.init(keyParams);
        final AsymmetricCipherKeyPair key = generator.generateKeyPair();
        ECPrivateKeyParameters privateKeyParameters = (ECPrivateKeyParameters) key.getPrivate();
        return Numeric.toHexStringWithPrefix(privateKeyParameters.getD());
    }
}
