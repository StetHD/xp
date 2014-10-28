package com.enonic.wem.api.security;

import com.google.common.base.Preconditions;

public final class User
    extends Principal
{
    private final static User ANONYMOUS = new User();

    private final String email;

    private final String login;

    private final boolean loginDisabled;

    private User( final Builder builder )
    {
        super( builder.principalKey, builder.displayName );
        Preconditions.checkArgument( builder.principalKey.isUser(), "Invalid Principal Type for User: " + builder.principalKey.getType() );
        this.email = builder.email;
        this.login = builder.login;
        this.loginDisabled = builder.loginDisabled;
    }

    private User()
    {
        super( PrincipalKey.ofAnonymous(), "anonymous" );
        this.email = "";
        this.login = "";
        this.loginDisabled = true;
    }

    public String getEmail()
    {
        return email;
    }

    public String getLogin()
    {
        return login;
    }

    public boolean isDisabled()
    {
        return loginDisabled;
    }

    public static Builder newUser()
    {
        return new Builder();
    }

    public static Builder newUser( final User user )
    {
        return new Builder( user );
    }

    public static User anonymous()
    {
        return ANONYMOUS;
    }

    public static class Builder
    {
        private PrincipalKey principalKey;

        private String displayName;

        private String email;

        private String login;

        private boolean loginDisabled;

        private Builder()
        {
        }

        private Builder( final User user )
        {
            this.principalKey = user.getKey();
            this.displayName = user.getDisplayName();
            this.email = user.getEmail();
            this.login = user.getLogin();
            this.loginDisabled = user.isDisabled();
        }

        public Builder userKey( final PrincipalKey value )
        {
            this.principalKey = value;
            return this;
        }

        public Builder displayName( final String value )
        {
            this.displayName = value;
            return this;
        }

        public Builder login( final String value )
        {
            this.login = value;
            return this;
        }

        public Builder email( final String value )
        {
            this.email = value;
            return this;
        }

        public User build()
        {
            return new User( this );
        }
    }

}
