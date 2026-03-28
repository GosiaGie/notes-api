package com.notes_api.entity;

import com.notes_api.security.UserPrincipal;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

public class UserRevisionListener implements RevisionListener {
    @Override
    public void newRevision(Object revisionEntityObj) {

        CustomRevisionEntity revisionEntity = (CustomRevisionEntity) revisionEntityObj;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal user) {
            String username = user.getUsername();
            if (StringUtils.hasText(username)) {
                revisionEntity.setChangedBy(username);
                return;
            }
        }
        throw new IllegalStateException("Error - missing info about user");
    }
}