/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package org.radarcns.phonesensors;  
@SuppressWarnings("all")
/** Data from 3-axis accelerometer sensor with gravitational constant g as unit. */
@org.apache.avro.specific.AvroGenerated
public class PhoneSensorAcceleration extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"PhoneSensorAcceleration\",\"namespace\":\"org.radarcns.phonesensors\",\"doc\":\"Data from 3-axis accelerometer sensor with gravitational constant g as unit.\",\"fields\":[{\"name\":\"time\",\"type\":\"double\",\"doc\":\"device timestamp in UTC (s)\"},{\"name\":\"timeReceived\",\"type\":\"double\",\"doc\":\"device receiver timestamp in UTC (s)\"},{\"name\":\"x\",\"type\":\"float\",\"doc\":\"acceleration in the x-axis (g)\"},{\"name\":\"y\",\"type\":\"float\",\"doc\":\"acceleration in the y-axis (g)\"},{\"name\":\"z\",\"type\":\"float\",\"doc\":\"acceleration in the z-axis (g)\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  /** device timestamp in UTC (s) */
  @Deprecated public double time;
  /** device receiver timestamp in UTC (s) */
  @Deprecated public double timeReceived;
  /** acceleration in the x-axis (g) */
  @Deprecated public float x;
  /** acceleration in the y-axis (g) */
  @Deprecated public float y;
  /** acceleration in the z-axis (g) */
  @Deprecated public float z;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>. 
   */
  public PhoneSensorAcceleration() {}

  /**
   * All-args constructor.
   */
  public PhoneSensorAcceleration(java.lang.Double time, java.lang.Double timeReceived, java.lang.Float x, java.lang.Float y, java.lang.Float z) {
    this.time = time;
    this.timeReceived = timeReceived;
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return time;
    case 1: return timeReceived;
    case 2: return x;
    case 3: return y;
    case 4: return z;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: time = (java.lang.Double)value$; break;
    case 1: timeReceived = (java.lang.Double)value$; break;
    case 2: x = (java.lang.Float)value$; break;
    case 3: y = (java.lang.Float)value$; break;
    case 4: z = (java.lang.Float)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'time' field.
   * device timestamp in UTC (s)   */
  public java.lang.Double getTime() {
    return time;
  }

  /**
   * Sets the value of the 'time' field.
   * device timestamp in UTC (s)   * @param value the value to set.
   */
  public void setTime(java.lang.Double value) {
    this.time = value;
  }

  /**
   * Gets the value of the 'timeReceived' field.
   * device receiver timestamp in UTC (s)   */
  public java.lang.Double getTimeReceived() {
    return timeReceived;
  }

  /**
   * Sets the value of the 'timeReceived' field.
   * device receiver timestamp in UTC (s)   * @param value the value to set.
   */
  public void setTimeReceived(java.lang.Double value) {
    this.timeReceived = value;
  }

  /**
   * Gets the value of the 'x' field.
   * acceleration in the x-axis (g)   */
  public java.lang.Float getX() {
    return x;
  }

  /**
   * Sets the value of the 'x' field.
   * acceleration in the x-axis (g)   * @param value the value to set.
   */
  public void setX(java.lang.Float value) {
    this.x = value;
  }

  /**
   * Gets the value of the 'y' field.
   * acceleration in the y-axis (g)   */
  public java.lang.Float getY() {
    return y;
  }

  /**
   * Sets the value of the 'y' field.
   * acceleration in the y-axis (g)   * @param value the value to set.
   */
  public void setY(java.lang.Float value) {
    this.y = value;
  }

  /**
   * Gets the value of the 'z' field.
   * acceleration in the z-axis (g)   */
  public java.lang.Float getZ() {
    return z;
  }

  /**
   * Sets the value of the 'z' field.
   * acceleration in the z-axis (g)   * @param value the value to set.
   */
  public void setZ(java.lang.Float value) {
    this.z = value;
  }

  /** Creates a new PhoneSensorAcceleration RecordBuilder */
  public static org.radarcns.phonesensors.PhoneSensorAcceleration.Builder newBuilder() {
    return new org.radarcns.phonesensors.PhoneSensorAcceleration.Builder();
  }
  
  /** Creates a new PhoneSensorAcceleration RecordBuilder by copying an existing Builder */
  public static org.radarcns.phonesensors.PhoneSensorAcceleration.Builder newBuilder(org.radarcns.phonesensors.PhoneSensorAcceleration.Builder other) {
    return new org.radarcns.phonesensors.PhoneSensorAcceleration.Builder(other);
  }
  
  /** Creates a new PhoneSensorAcceleration RecordBuilder by copying an existing PhoneSensorAcceleration instance */
  public static org.radarcns.phonesensors.PhoneSensorAcceleration.Builder newBuilder(org.radarcns.phonesensors.PhoneSensorAcceleration other) {
    return new org.radarcns.phonesensors.PhoneSensorAcceleration.Builder(other);
  }
  
  /**
   * RecordBuilder for PhoneSensorAcceleration instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<PhoneSensorAcceleration>
    implements org.apache.avro.data.RecordBuilder<PhoneSensorAcceleration> {

    private double time;
    private double timeReceived;
    private float x;
    private float y;
    private float z;

    /** Creates a new Builder */
    private Builder() {
      super(org.radarcns.phonesensors.PhoneSensorAcceleration.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(org.radarcns.phonesensors.PhoneSensorAcceleration.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.time)) {
        this.time = data().deepCopy(fields()[0].schema(), other.time);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.timeReceived)) {
        this.timeReceived = data().deepCopy(fields()[1].schema(), other.timeReceived);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.x)) {
        this.x = data().deepCopy(fields()[2].schema(), other.x);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.y)) {
        this.y = data().deepCopy(fields()[3].schema(), other.y);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.z)) {
        this.z = data().deepCopy(fields()[4].schema(), other.z);
        fieldSetFlags()[4] = true;
      }
    }
    
    /** Creates a Builder by copying an existing PhoneSensorAcceleration instance */
    private Builder(org.radarcns.phonesensors.PhoneSensorAcceleration other) {
            super(org.radarcns.phonesensors.PhoneSensorAcceleration.SCHEMA$);
      if (isValidValue(fields()[0], other.time)) {
        this.time = data().deepCopy(fields()[0].schema(), other.time);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.timeReceived)) {
        this.timeReceived = data().deepCopy(fields()[1].schema(), other.timeReceived);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.x)) {
        this.x = data().deepCopy(fields()[2].schema(), other.x);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.y)) {
        this.y = data().deepCopy(fields()[3].schema(), other.y);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.z)) {
        this.z = data().deepCopy(fields()[4].schema(), other.z);
        fieldSetFlags()[4] = true;
      }
    }

    /** Gets the value of the 'time' field */
    public java.lang.Double getTime() {
      return time;
    }
    
    /** Sets the value of the 'time' field */
    public org.radarcns.phonesensors.PhoneSensorAcceleration.Builder setTime(double value) {
      validate(fields()[0], value);
      this.time = value;
      fieldSetFlags()[0] = true;
      return this; 
    }
    
    /** Checks whether the 'time' field has been set */
    public boolean hasTime() {
      return fieldSetFlags()[0];
    }
    
    /** Clears the value of the 'time' field */
    public org.radarcns.phonesensors.PhoneSensorAcceleration.Builder clearTime() {
      fieldSetFlags()[0] = false;
      return this;
    }

    /** Gets the value of the 'timeReceived' field */
    public java.lang.Double getTimeReceived() {
      return timeReceived;
    }
    
    /** Sets the value of the 'timeReceived' field */
    public org.radarcns.phonesensors.PhoneSensorAcceleration.Builder setTimeReceived(double value) {
      validate(fields()[1], value);
      this.timeReceived = value;
      fieldSetFlags()[1] = true;
      return this; 
    }
    
    /** Checks whether the 'timeReceived' field has been set */
    public boolean hasTimeReceived() {
      return fieldSetFlags()[1];
    }
    
    /** Clears the value of the 'timeReceived' field */
    public org.radarcns.phonesensors.PhoneSensorAcceleration.Builder clearTimeReceived() {
      fieldSetFlags()[1] = false;
      return this;
    }

    /** Gets the value of the 'x' field */
    public java.lang.Float getX() {
      return x;
    }
    
    /** Sets the value of the 'x' field */
    public org.radarcns.phonesensors.PhoneSensorAcceleration.Builder setX(float value) {
      validate(fields()[2], value);
      this.x = value;
      fieldSetFlags()[2] = true;
      return this; 
    }
    
    /** Checks whether the 'x' field has been set */
    public boolean hasX() {
      return fieldSetFlags()[2];
    }
    
    /** Clears the value of the 'x' field */
    public org.radarcns.phonesensors.PhoneSensorAcceleration.Builder clearX() {
      fieldSetFlags()[2] = false;
      return this;
    }

    /** Gets the value of the 'y' field */
    public java.lang.Float getY() {
      return y;
    }
    
    /** Sets the value of the 'y' field */
    public org.radarcns.phonesensors.PhoneSensorAcceleration.Builder setY(float value) {
      validate(fields()[3], value);
      this.y = value;
      fieldSetFlags()[3] = true;
      return this; 
    }
    
    /** Checks whether the 'y' field has been set */
    public boolean hasY() {
      return fieldSetFlags()[3];
    }
    
    /** Clears the value of the 'y' field */
    public org.radarcns.phonesensors.PhoneSensorAcceleration.Builder clearY() {
      fieldSetFlags()[3] = false;
      return this;
    }

    /** Gets the value of the 'z' field */
    public java.lang.Float getZ() {
      return z;
    }
    
    /** Sets the value of the 'z' field */
    public org.radarcns.phonesensors.PhoneSensorAcceleration.Builder setZ(float value) {
      validate(fields()[4], value);
      this.z = value;
      fieldSetFlags()[4] = true;
      return this; 
    }
    
    /** Checks whether the 'z' field has been set */
    public boolean hasZ() {
      return fieldSetFlags()[4];
    }
    
    /** Clears the value of the 'z' field */
    public org.radarcns.phonesensors.PhoneSensorAcceleration.Builder clearZ() {
      fieldSetFlags()[4] = false;
      return this;
    }

    @Override
    public PhoneSensorAcceleration build() {
      try {
        PhoneSensorAcceleration record = new PhoneSensorAcceleration();
        record.time = fieldSetFlags()[0] ? this.time : (java.lang.Double) defaultValue(fields()[0]);
        record.timeReceived = fieldSetFlags()[1] ? this.timeReceived : (java.lang.Double) defaultValue(fields()[1]);
        record.x = fieldSetFlags()[2] ? this.x : (java.lang.Float) defaultValue(fields()[2]);
        record.y = fieldSetFlags()[3] ? this.y : (java.lang.Float) defaultValue(fields()[3]);
        record.z = fieldSetFlags()[4] ? this.z : (java.lang.Float) defaultValue(fields()[4]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}