package com.zeldaspeedruns.zeldaspeedruns.organizations;

import com.github.slugify.Slugify;

import java.util.List;

public class OrganizationTestUtils {
    private final static Slugify slugify = new Slugify();

    public static Organization organization(String name) {
        return organization(name, List.of("Tracker", "Commentator", "Restreamer"));
    }

    public static Organization organization(String name, List<String> roles) {
        return organization(name, slugify.slugify(name), roles);
    }

    public static Organization organization(String name, String slug, List<String> roles) {
        var organization = new Organization(name, slug);
        roles.stream()
                .map(r -> new OrganizationRole(organization, r, slugify.slugify(r)))
                .forEach(r -> organization.getRoles().add(r));

        return organization;
    }
}
