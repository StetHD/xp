package com.enonic.xp.query.expr;

import com.google.common.annotations.Beta;

import com.enonic.xp.index.IndexPath;

@Beta
public final class FieldOrderExpr
    extends OrderExpr
{
    private final FieldExpr field;

    public FieldOrderExpr( final FieldExpr field, final Direction direction )
    {
        super( direction );
        this.field = field;
    }

    public FieldExpr getField()
    {
        return this.field;
    }

    public static FieldOrderExpr create( final IndexPath indexPath, Direction direction )
    {
        return new FieldOrderExpr( FieldExpr.from( indexPath ), direction );
    }

    @Override
    public String toString()
    {
        return this.field + " " + getDirection();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        if ( !super.equals( o ) )
        {
            return false;
        }

        final FieldOrderExpr that = (FieldOrderExpr) o;

        if ( field != null ? !field.equals( that.field ) : that.field != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + ( field != null ? field.hashCode() : 0 );
        return result;
    }
}
