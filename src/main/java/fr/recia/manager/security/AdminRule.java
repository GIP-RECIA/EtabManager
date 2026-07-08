package fr.recia.manager.security;

import lombok.Data;

import java.util.List;
import java.util.regex.Pattern;

@Data
public class AdminRule {
    private String pattern;
    private Pattern compiledPattern;
    private ExtractType extract;
    private List<AppRole> roles;
}
