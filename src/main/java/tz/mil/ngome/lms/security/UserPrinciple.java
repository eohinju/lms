package tz.mil.ngome.lms.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;

import tz.mil.ngome.lms.entity.User;

@Component
public class UserPrinciple implements UserDetails {
	  	private static final long serialVersionUID = 1L;
	 
	  	private String id;
	 
	    private String name;
	 
	    private String username;
	 
	    private String email;
	    
	    @JsonIgnore
	    private String password;
	    
	    private Collection<? extends GrantedAuthority> authorities;

	    public UserPrinciple(String id, String name, String username, String email, String password, List<GrantedAuthority> authorities) {
	    	this.id = id;
	    	this.setName(name);
	    	this.username = username;
	    	this.setEmail(email);
	    	this.password = password;
	    	this.authorities = authorities;
	    }
	    
	    public UserPrinciple() {
			super();
		}

		public static UserPrinciple build(User user) {
	        List<GrantedAuthority> authorities = new ArrayList<>();
	        authorities.add(new SimpleGrantedAuthority(user.getRole().name()));
	 
	        return new UserPrinciple(
	                user.getId(),
	                user.getName(),
	                user.getUsername(),
	                user.getEmail(),
	                user.getPassword(),
	                authorities
	        );
	    }
		
	    @Override
	    public String getUsername() {
	        return username;
	    }
	 
	    @Override
	    public String getPassword() {
	        return password;
	    }
	 
	    @Override
	    public Collection<? extends GrantedAuthority> getAuthorities() {
	        return authorities;
	    }
	 
	    @Override
	    public boolean isAccountNonExpired() {
	        return true;
	    }
	 
	    @Override
	    public boolean isAccountNonLocked() {
	        return true;
	    }
	 
	    @Override
	    public boolean isCredentialsNonExpired() {
	        return true;
	    }
	 
	    @Override
	    public boolean isEnabled() {
	        return true;
	    }
	 
	    @Override
	    public boolean equals(Object o) {
	        if (this == o) return true;
	        if (o == null || getClass() != o.getClass()) return false;
	        
	        UserPrinciple user = (UserPrinciple) o;
	        return Objects.equals(id, user.id);
	    }
	    
	    public String getId() {
	    	return this.id;
	    }

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}
	    
	}
