import ValueTypeConverter = api.data.ValueTypeConverter;

describe("api.data.ValueTypeConverter", () => {

    describe("to string conversion test", () => {

        it("number value should be converted to string", () => {
            var convertableNumberValue = new Value(1, ValueTypes.LONG);
            var converted = ValueTypeConverter.convertTo(convertableNumberValue, ValueTypes.STRING);

            expect(converted.getString()).toBe("1");
        });

        it("localdatetime value should be converted to string", () => {
            var convertableDateValue = ValueTypes.LOCAL_DATE_TIME.newValue("2011-04-11T11:51:00");
            var converted = ValueTypeConverter.convertTo(convertableDateValue, ValueTypes.STRING);

            expect(converted.getString()).not.toBeNull();
        });
    });

    describe("to data conversion test", () => {

        it("string value should be converted to data", () => {
            var convertableValue = new Value("XxX", ValueTypes.STRING);
            var converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.DATA);

            expect(converted.getPropertySet().isDetached()).toBeTruthy();
            expect(converted.getObject()).not.toBeNull();
        });
    });

    describe("to xml conversion test", () => {

        it("string value should be converted to xml", () => {
            var convertableValue = new Value("XxX", ValueTypes.STRING);
            var converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.XML);

            expect(converted.getString()).toBe("XxX");
        });
        ;
    });

    describe("to local date conversion test", () => {

        it("string value should be converted to local date", () => {
            var convertableValue = new Value("2015-01-01", ValueTypes.STRING);
            var converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.LOCAL_DATE);

            expect(converted.getString()).toBe("2015-01-01");
        });

        it("localdatetime value should be converted to local date", () => {
            var convertableValue = ValueTypes.LOCAL_DATE_TIME.newValue("2011-04-11T00:00:00");
            var converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.LOCAL_DATE);

            expect(converted.getString()).toBe("2011-04-11");
        });

        it("datetime value should be converted to local date", () => {
            var convertableValue = ValueTypes.DATE_TIME.newValue("2011-04-11T00:00:00");
            var converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.LOCAL_DATE);

            expect(converted.getString()).toBe("2011-04-11");
        });

        it("bad value value should be converted to null local date", () => {
            var convertableValue = ValueTypes.STRING.newValue("bad");
            var converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.LOCAL_DATE);

            expect(converted.getObject()).toBeNull();
        });
    });

    describe("to local time conversion test", () => {

        it("string value should be converted to local time", () => {
            var convertableValue = new Value("20:00", ValueTypes.STRING);
            var converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.LOCAL_TIME);

            expect(converted.getString()).toBe("20:00");
        });

        it("localdatetime value should be converted to local time", () => {
            var convertableValue = ValueTypes.LOCAL_DATE_TIME.newValue("2011-04-11T11:51:00");
            var converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.LOCAL_TIME);

            expect(converted.getString()).toBe("11:51");
        });

        it("datetime value should be converted to local time", () => {
            var convertableValue = ValueTypes.DATE_TIME.newValue("2011-04-11T11:51:00");
            var converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.LOCAL_TIME);

            expect(converted.getString()).toBe("11:51");
        });

        it("bad value value should be converted to null local time", () => {
            var convertableValue = ValueTypes.STRING.newValue("bad");
            var converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.LOCAL_TIME);

            expect(converted.getObject()).toBeNull();
        });
    });

    describe("to local datetime conversion test", () => {

        it("string value should be converted to local datetime", () => {
            var convertableValue = new Value("2015-01-01T15:00:01", ValueTypes.STRING);
            var converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.LOCAL_DATE_TIME);

            expect(converted.getString()).toBe("2015-01-01T15:00:01");
        });

        it("localdate value should be converted to local datetime", () => {
            var convertableValue = ValueTypes.LOCAL_DATE.newValue("2011-04-11");
            var converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.LOCAL_DATE_TIME);

            expect(converted.getString()).toBe("2011-04-11T00:00:00");
        });

        it("bad localdate value should be converted to local datetime", () => {
            var convertableValue = ValueTypes.LOCAL_DATE.newValue("bad");
            var converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.LOCAL_DATE_TIME);

            expect(converted.getString()).toBeNull();
        });

        it("datetime value should be converted to local datetime", () => {
            var convertableValue = ValueTypes.DATE_TIME.newValue("2011-04-11T11:51:00");
            var converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.LOCAL_DATE_TIME);

            expect(converted.getString()).not.toBeNull();
        });

        it("bad value value should be converted to null local datetime", () => {
            var convertableValue = ValueTypes.STRING.newValue("bad");
            var converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.LOCAL_DATE_TIME);

            expect(converted.getObject()).toBeNull();
        });
    });

    describe("to datetime conversion test", () => {

        it("string value should be converted to datetime", () => {
            var convertableValue = new Value("2015-01-01T15:00:01", ValueTypes.STRING);
            var converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.DATE_TIME);

            expect(converted.getString()).toBe("2015-01-01T15:00:01+00:00");
        });

        it("localdate value should be converted to datetime", () => {
            var convertableValue = ValueTypes.LOCAL_DATE.newValue("2011-04-11");
            var converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.DATE_TIME);

            expect(converted.getString()).toBe("2011-04-11T00:00:00+00:00");
        });

        it("local datetime value should be converted to datetime", () => {
            var convertableValue = ValueTypes.LOCAL_DATE_TIME.newValue("2011-04-11T11:51:00");
            var converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.DATE_TIME);

            expect(converted.getString()).toBe("2011-04-11T11:51:00+00:00");
        });

        it("bad value value should be converted to null datetime", () => {
            var convertableValue = ValueTypes.STRING.newValue("bad");
            var converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.DATE_TIME);

            expect(converted.getObject()).toBeNull();
        });
    });

    describe("to long conversion test", () => {

        it("string value should be converted to long", () => {
            var convertableValue = new Value("100", ValueTypes.STRING);
            var converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.LONG);

            expect(converted.getString()).toBe("100");
        });

        it("double value should be converted to long", () => {
            var convertableValue = ValueTypes.DOUBLE.newValue("200.00");
            var converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.LONG);

            expect(converted.getString()).toBe("200");
        });

        it("boolean value should be converted to long", () => {
            var convertableValue = ValueTypes.BOOLEAN.newValue("true");
            var converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.LONG);

            expect(converted.getString()).toBe("1");
        });

        it("bad value value should be converted to null long", () => {
            var convertableValue = ValueTypes.STRING.newValue("bad");
            var converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.LONG);

            expect(converted.getObject()).toBeNull();
        });
    });

    describe("to double conversion test", () => {

        it("string value should be converted to double", () => {
            var convertableValue = new Value("100", ValueTypes.STRING);
            var converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.DOUBLE);

            expect(converted.getString()).toBe("100");
        });

        it("long value should be converted to double", () => {
            var convertableValue = ValueTypes.LONG.newValue("200");
            var converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.DOUBLE);

            expect(converted.getString()).toBe("200");
        });

        it("boolean value should be converted to long", () => {
            var convertableValue = ValueTypes.BOOLEAN.newValue("true");
            var converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.DOUBLE);

            expect(converted.getString()).toBe("1");
        });

        it("bad value value should be converted to null long", () => {
            var convertableValue = ValueTypes.STRING.newValue("bad");
            var converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.DOUBLE);

            expect(converted.getObject()).toBeNull();
        });
    });
});