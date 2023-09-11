
// DO NOT CHANGE PACKAGE !!!
package rmi.digest;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

/**
 * Remote interface for RMIDigestValidator service.
 * 
 * @author Rudolf Scheurer (HEIA-FR)
 * @version 1.23 (03.2023)
 *
 */
public interface RMIDigestValidator extends Remote {

  String getChallenge(String username) throws RemoteException;
  boolean challengeResponse(String username, byte[] hash) throws RemoteException;

}
