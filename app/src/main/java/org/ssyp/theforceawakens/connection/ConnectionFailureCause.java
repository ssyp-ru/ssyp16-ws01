package org.ssyp.theforceawakens.connection;

/**
 * Created by Arthurii on 27.07.2016.
 */
public enum ConnectionFailureCause {
    Disconnected, // For manual close
    ServerBusy,
    NicknameTaken,
    ConnectTimeout,
    ReadFailed,
    ReadNull,
    ReadParseFailed,
    Write,
    SocketCreationIOException, //IOException at ...new socket(...
    WrongIP,
    AcknowledgeTimeout
}
