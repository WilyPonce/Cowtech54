package com.sourcey.cowtech54; //com.amazonaws.models.nosql;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

//@DynamoDBTable(tableName = "cowtech-mobilehub-304479169-Truck4.0")
@DynamoDBTable(tableName = "Truck4.0")

public class Truck40DO {
    private String _truckID;
    private Double _timeStamp;
    private Double _byte1;
    private Double _byte2;
    private Double _byte3;
    private Double _byte4;
    private Double _byte5;
    private Double _byte6;
    private Double _byte7;
    private Double _byte8;
    private Double _canID;
    private Double _lat;
    private Set<Double> _latLong;
    private Double _len;
    private Double _long;

    @DynamoDBHashKey(attributeName = "TruckID")
    @DynamoDBAttribute(attributeName = "TruckID")
    public String getTruckID() {
        return _truckID;
    }

    public void setTruckID(final String _truckID) {
        this._truckID = _truckID;
    }
    @DynamoDBRangeKey(attributeName = "TimeStamp")
    @DynamoDBIndexRangeKey(attributeName = "TimeStamp", globalSecondaryIndexName = "CanID-TimeStamp")
    public Double getTimeStamp() {
        return _timeStamp;
    }

    public void setTimeStamp(final Double _timeStamp) {
        this._timeStamp = _timeStamp;
    }
    @DynamoDBAttribute(attributeName = "Byte1")
    public Double getByte1() {
        return _byte1;
    }

    public void setByte1(final Double _byte1) {
        this._byte1 = _byte1;
    }
    @DynamoDBAttribute(attributeName = "Byte2")
    public Double getByte2() {
        return _byte2;
    }

    public void setByte2(final Double _byte2) {
        this._byte2 = _byte2;
    }
    @DynamoDBAttribute(attributeName = "Byte3")
    public Double getByte3() {
        return _byte3;
    }

    public void setByte3(final Double _byte3) {
        this._byte3 = _byte3;
    }
    @DynamoDBAttribute(attributeName = "Byte4")
    public Double getByte4() {
        return _byte4;
    }

    public void setByte4(final Double _byte4) {
        this._byte4 = _byte4;
    }
    @DynamoDBAttribute(attributeName = "Byte5")
    public Double getByte5() {
        return _byte5;
    }

    public void setByte5(final Double _byte5) {
        this._byte5 = _byte5;
    }
    @DynamoDBAttribute(attributeName = "Byte6")
    public Double getByte6() {
        return _byte6;
    }

    public void setByte6(final Double _byte6) {
        this._byte6 = _byte6;
    }
    @DynamoDBAttribute(attributeName = "Byte7")
    public Double getByte7() {
        return _byte7;
    }

    public void setByte7(final Double _byte7) {
        this._byte7 = _byte7;
    }
    @DynamoDBAttribute(attributeName = "Byte8")
    public Double getByte8() {
        return _byte8;
    }

    public void setByte8(final Double _byte8) {
        this._byte8 = _byte8;
    }
    @DynamoDBIndexHashKey(attributeName = "CanID", globalSecondaryIndexName = "CanID-TimeStamp")
    public Double getCanID() {
        return _canID;
    }

    public void setCanID(final Double _canID) {
        this._canID = _canID;
    }
    @DynamoDBAttribute(attributeName = "Lat")
    public Double getLat() {
        return _lat;
    }

    public void setLat(final Double _lat) {
        this._lat = _lat;
    }
    @DynamoDBAttribute(attributeName = "LatLong")
    public Set<Double> getLatLong() {
        return _latLong;
    }

    public void setLatLong(final Set<Double> _latLong) {
        this._latLong = _latLong;
    }
    @DynamoDBAttribute(attributeName = "Len")
    public Double getLen() {
        return _len;
    }

    public void setLen(final Double _len) {
        this._len = _len;
    }
    @DynamoDBAttribute(attributeName = "Long")
    public Double getLong() {
        return _long;
    }

    public void setLong(final Double _long) {
        this._long = _long;
    }

}
