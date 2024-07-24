package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Exception.ISOException;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.container.ISOBaseContainer;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.container.ISOContainer;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_handler.ISOBinaryHandler;

import java.text.SimpleDateFormat;
import java.util.*;

import static hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper.ConversionHelper.*;

public class ISOMsg extends ISOComponent implements Cloneable {
    protected Map<Integer, Object> headerFields;
    protected Map<Integer, Object> fields;
    protected int maxFields;
    protected ISOContainer container;
    protected int parentFieldNumber = -1;
    protected Object parentFieldNumberValue = new Object();

    protected StringBuilder logger;

    public ISOMsg() {
        headerFields = new TreeMap<>();
        fields = new TreeMap<>();
        maxFields = -1;
        this.logger = new StringBuilder();
    }

    public ISOMsg(int fldNo) {
        this();
        this.parentFieldNumber = fldNo;
    }

    @Override
    public void set(ISOComponent c) throws ISOException {
        if (c != null) {
            Integer i = (Integer) c.getFldNo();
            fields.put(i, c);
            if (i > maxFields) maxFields = i;
        }
    }

    @Override
    public void setHeader(ISOComponent c) throws ISOException {
        if (c != null) {
            Integer i = (Integer) c.getFldNo();
            headerFields.put(i, c);
        }
    }

    public void setHeader(int fldno, String value) {
        try {
            if (!(this.container instanceof ISOBaseContainer)) {
                this.setHeader(new ISOField(fldno, value));
            } else {
                Object obj = ((ISOBaseContainer)this.container).getFieldContainer(fldno);
                if (obj instanceof ISOBinaryHandler) {
                    this.setHeader(new ISOBinaryField(fldno, hex2byte(value, CHARSET)));
                } else {
                    this.setHeader(new ISOField(fldno, value));
                }
            }
        } catch (ISOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void set(int fldno, String value) {
        try {
            if (!(this.container instanceof ISOBaseContainer)) {
                this.set(new ISOField(fldno, value));
            } else {
                Object obj = ((ISOBaseContainer)this.container).getFieldContainer(fldno);
                if (obj instanceof ISOBinaryHandler) {
                    this.set(new ISOBinaryField(fldno, hex2byte(value, CHARSET)));
                } else {
                    this.set(new ISOField(fldno, value));
                }
            }
        } catch (ISOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void set(String fpath, String value) throws ISOException {
        StringTokenizer st = new StringTokenizer(fpath, ".");
        ISOMsg m = this;

        while (true) {
            int fldNo = this.parseInt(st.nextToken());
            if (!st.hasMoreTokens()) {
                m.set(fldNo, value);
                break;
            }

            Object obj = m.getValue(fldNo);
            if (obj instanceof ISOMsg) {
                m = (ISOMsg) obj;
            } else {
                if (value == null) {
                    break;
                }

                try {
                    m.set(m = new ISOMsg(fldNo));
                } catch (ISOException e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Object getFldNo() throws ISOException {
        if (parentFieldNumber != -1) return parentFieldNumber;
        throw new ISOException("This is not subfield");
    }

    public void createBitmap(int fldNo) throws ISOException {
        int mf = Math.min(this.getMaxField(), 192);
        BitSet bmap = new BitSet(mf + 62 >> 6 << 6);

        this.fields.forEach((key, value) -> {
            if (key != 0 && key != 1) bmap.set(key);
        });

        this.set(new ISOBitmap(fldNo, bmap));
    }

    @Override
    public byte[] pack() throws ISOException {
        synchronized (this) {
            this.createBitmap(1);
            byte[] packedValue = container.pack(this);
            this.setLoggerMessage("");
            this.setLoggerMessage("Extract raw data: ");
            this.setLoggerMessage(formatHexDump(packedValue, 0, packedValue.length));

            return packedValue;
        }
    }

    @Override
    public int unpack(byte[] b) throws ISOException {
        this.setLoggerMessage(formatHexDump(b, 0, b.length));
        this.setLoggerMessage("");
        this.setLoggerMessage("Extract parsed data: ");
        synchronized (this) {
            return container.unpack(this, b);
        }
    }

    @Override
    public Object getValue() throws ISOException {
        return this;
    }

    public void setParentFieldNumberValue(Object parentFieldNumberValue) {
        this.parentFieldNumberValue = parentFieldNumberValue;
    }

    public void setContainer(ISOContainer container) {
        this.container = container;
    }

    public ISOContainer getContainer() {
        return this.container;
    }

    public Object getParentFieldNumberValue() {
        return parentFieldNumberValue;
    }

    public int getParentFieldNumber() {
        return parentFieldNumber;
    }

    public void setParentFieldNumber(int parentFieldNumber) {
        this.parentFieldNumber = parentFieldNumber;
    }

    public String getString(String fpath) {
        String s = null;

        try {
            Object obj = this.getValue(fpath);
            if (obj instanceof String) {
                s = (String) obj;
            } else if (obj instanceof byte[]) {
                s = hexString((byte[]) obj);
            } else if (obj instanceof ISOMsg) {
                s = ((ISOMsg) obj).getParentFieldNumberValue().toString();
            } else if (obj instanceof BitSet) {
                s = obj.toString();
            }

            return s;
        } catch (Exception e) {
            return null;
        }
    }

    public Object getValue(String fpath) {
        StringTokenizer st = new StringTokenizer(fpath, ".");
        ISOMsg m = this;

        while (true) {
            int fldNo = this.parseInt(st.nextToken());
            Object obj = m.getValue(fldNo);

            if (obj == null || !st.hasMoreTokens()) {
                return obj;
            }

            if (!(obj instanceof ISOMsg)) {
                throw new IllegalArgumentException("Invalid path '" + fpath + "'");
            }

            m = (ISOMsg) obj;
        }
    }

    public Object getValue(int fldno) {
        ISOComponent c = this.getComponent(fldno);

        try {
            return c != null ? c.getValue() : null;
        } catch (Exception var4) {
            return null;
        }
    }



    public ISOComponent getComponent(int fdlNo) {
        return (ISOComponent) this.fields.get(fdlNo);
    }

    @Override
    public int getMaxField() {
        this.maxFields = 0;
        Iterator var1 = this.fields.keySet().iterator();

        while (var1.hasNext()) {
            Object obj = var1.next();
            if (obj instanceof Integer) {
                this.maxFields = Math.max(this.maxFields, (Integer) obj);
            }
        }
        return this.maxFields;
    }

    @Override
    public Map<Integer, Object> getChildren(int type) {
        switch (type) {
            case 1:
                return (Map)((TreeMap)this.fields).clone();
            case 2:
                return (Map)((TreeMap)this.headerFields).clone();
            default:
                return new TreeMap<>();
        }
    }

    private int parseInt(String s) {
        return s.startsWith("0x") ? Integer.parseInt(s.substring(2), 16) : Integer.parseInt(s);
    }

    public ISOMsg clone() {
        try {
            ISOMsg m = (ISOMsg) super.clone();
            m.fields = this.getChildren(1);
            m.headerFields = this.getChildren(2);
            m.logger = new StringBuilder();
            return m;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public StringBuilder getLogger() {
        return logger;
    }

    public void setLogger(StringBuilder logger) {
        this.logger = logger;
    }

    public void setLoggerMessage(String message) {
        this.logger
                .append(message).append("\n");
    }

    public void setLoggerMessageWithoutTimeStamp(String message) {
        this.logger
                .append(message);
    }

    public void setLoggerMessageWithoutTimeStampNL(String message) {
        this.logger
                .append(message)
                .append("\n");
    }

    public void setLoggerMessageWithoutNewLine(String message) {
        this.logger.append(message);
    }

    @Override
    public void setValue(Object obj) {
        //NO NEED
    }

    @Override
    public void setDescription(String description) {
        //NO NEED
    }
}
