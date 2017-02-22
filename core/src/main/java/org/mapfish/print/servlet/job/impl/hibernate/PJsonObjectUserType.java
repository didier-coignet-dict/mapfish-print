package org.mapfish.print.servlet.job.impl.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;
import org.json.JSONException;
import org.json.JSONObject;
import org.mapfish.print.wrapper.json.PJsonObject;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Hibernate User Type for PJson object.
 *
 */
public class PJsonObjectUserType implements UserType {

    private static final int[] SQL_TYPES = { Types.LONGVARCHAR };
    
    private static final String CONTEXT_NAME = "spec";

    @Override
    public final Object assemble(final Serializable cached, final Object owner) throws HibernateException {
        return deepCopy(cached);
    }

    @Override
    public final Object deepCopy(final Object value) throws HibernateException {
        if (value == null) {
            return value;
        } else {
            try {
                return new PJsonObject(new JSONObject(((PJsonObject) value).getInternalObj().toString()), CONTEXT_NAME);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public final Serializable disassemble(final Object value) throws HibernateException {
        return (Serializable) deepCopy(value);
    }

    @Override
    public final boolean equals(final Object x, final Object y) throws HibernateException {
        if (x == null) {
            return (y != null);
        } else {
            return (x.equals(y));
        }
    }

    @Override
    public final int hashCode(final Object x) throws HibernateException {
        return ((PJsonObject) x).hashCode();
    }

    @Override
    public final boolean isMutable() {
        return false;
    }

    @Override
    public final Object nullSafeGet(final ResultSet rs, final String[] names, final SessionImplementor session,
            final Object owner) throws HibernateException, SQLException {
        String value = rs.getString(names[0]);
        if (value != null) {
            try {
                return new PJsonObject(new JSONObject(value), CONTEXT_NAME);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @Override
    public final void nullSafeSet(final PreparedStatement st, final Object value, final int index,
            final SessionImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, SQL_TYPES[0]);
        } else {
            st.setString(index, ((PJsonObject) value).getInternalObj().toString());
        }
    }

    @Override
    public final Object replace(final Object original, final Object target, final Object owner)
            throws HibernateException {
        return deepCopy(original);
    }

    @Override
    public final Class<PJsonObject> returnedClass() {
        return PJsonObject.class;
    }

    @Override
    public final int[] sqlTypes() {
        return SQL_TYPES;
    }

}