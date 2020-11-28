package edu.carleton.cas.utility;

import edu.carleton.cas.constants.ClientShared;
import edu.carleton.cas.logging.Logger;
import edu.carleton.cas.security.CryptoException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.logging.Level;

public abstract class CodeVerifier {
  public static void verify(Class<?> clazz) throws Throwable {
    if (ClientShared.CODE_MUST_BE_SIGNED || ClientShared.VERIFY_CODE_SIGNATURE) {
      Certificate[] certs = clazz.getProtectionDomain().getCodeSource().getCertificates();
      if (certs == null) {
        Logger.log(Level.INFO, "No signature for ", clazz.getCanonicalName());
        if (ClientShared.CODE_MUST_BE_SIGNED)
          throw new SignatureException("No signature found for " + clazz.getCanonicalName()); 
      } else if (ClientShared.VERIFY_CODE_SIGNATURE) {
        if (ClientShared.PUBLIC_KEY != null) {
          certs[0].verify(getKey(ClientShared.PUBLIC_KEY));
        } else {
          certs[0].verify(certs[0].getPublicKey());
        } 
        Logger.log(Level.CONFIG, "Code signature was verified for ", clazz.getCanonicalName());
      } 
    } 
  }
  
  public static PublicKey getKey(String key) throws CryptoException {
    try {
      byte[] byteKey = Base64.getDecoder().decode(key.getBytes());
      X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
      KeyFactory kf = KeyFactory.getInstance("RSA");
      return kf.generatePublic(X509publicKey);
    } catch (Exception e) {
      throw new CryptoException("Cannot obtain public key: ", e);
    } 
  }
}
