package pl.jalokim.propertiestojson.resolvers.hierarchy;

import lombok.Data;
import pl.jalokim.propertiestojson.util.exception.ParsePropertiesException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static pl.jalokim.propertiestojson.util.ListUtil.everyElementAsNewLine;

public class HierarchyClassResolver {

    private final String ERROR_MSG = "Found %s resolvers for instance type: %s%nfound resolvers:%n%s";

    private final List<Class<?>> resolverClasses;

    public HierarchyClassResolver(List<Class<?>> typesWhichCanResolve) {
        this.resolverClasses = typesWhichCanResolve;
    }

    public Class<?> searchResolverClass(Object instance) {
        if(instance == null) {
            return null;
        }
        final Class<?> instanceClass = instance.getClass();
        SearchContext searchContext = new SearchContext();

        for(Class<?> resolverClass : resolverClasses) {
            if(resolverClass.isAssignableFrom(instanceClass)) {
                if(!resolverClass.isInterface()) {
                    int difference = countDifferenceForSuperClass(resolverClass, instanceClass);
                    addToFoundWhenIsClosetsMatch(searchContext, resolverClass, difference);
                }
                if(resolverClass.isInterface()) {
                    findDifferenceInSuperClasses(searchContext, resolverClass, instanceClass);
                    findDifferenceForSuperInterfaces(searchContext, resolverClass, getSuperInterfaces(instanceClass));
                }
            }
        }

        if(searchContext.getFoundClasses().isEmpty()) {
            return null;
        }
        if(searchContext.getFoundClasses().size() == 1) {
            return searchContext.getFoundClasses().get(0);
        }

        List<Class<?>> foundClasses = searchContext.getFoundClasses();
         Optional<Class<?>> onlyObjectTypeOpt = foundClasses.stream()
                                                           .filter(type -> !type.isInterface() && !type.isEnum())
                                                           .findAny();
        if (onlyObjectTypeOpt.isPresent()) {
            if (onlyObjectTypeOpt.get() != Object.class) {
                return onlyObjectTypeOpt.get();
            }
            List<Class<?>> foundInterfaces = new ArrayList<>(foundClasses);
            foundInterfaces.remove(onlyObjectTypeOpt.get());

            if (foundInterfaces.size() == 1) {
                return foundInterfaces.get(0);
            }
            foundClasses = foundInterfaces;
        }

        throw new ParsePropertiesException(String.format(ERROR_MSG,
                                                         foundClasses.size(),
                                                         instance.getClass().getCanonicalName(),
                                                         everyElementAsNewLine(foundClasses)));
    }

    private void addToFoundWhenIsClosetsMatch(SearchContext searchContext, Class<?> resolverClass, int difference) {
        if(difference < searchContext.getCurrentNearCounter()) {
            searchContext.setCurrentNearCounter(difference);
            searchContext.clear();
            searchContext.add(resolverClass);
        } else if(searchContext.getCurrentNearCounter() == difference) {
            searchContext.add(resolverClass);
        }
    }

    private int countDifferenceForSuperClass(Class<?> resolverClass, Class<?> instanceClass) {
        Class<?> currentClass = instanceClass;
        int counter = 0;
        while(!resolverClass.getCanonicalName().equals(currentClass.getCanonicalName())) {
            currentClass = currentClass.getSuperclass();
            counter++;
            if (currentClass == null) {
                return Integer.MAX_VALUE;
            }
        }
        return counter;
    }

    private void findDifferenceInSuperClasses(final SearchContext searchContext,final Class<?> resolverClass, final Class<?> instanceClass) {
        Class<?> currentClass = instanceClass;
        while(currentClass != null) {
            searchContext.searchHierarchyLevel++;
            currentClass = currentClass.getSuperclass();
            if (currentClass == null) {
                break;
            }
            findDifferenceForSuperInterfaces(searchContext, resolverClass, getSuperInterfaces(currentClass));
        }
        searchContext.searchHierarchyLevel = 0;
    }

    private void findDifferenceForSuperInterfaces(final SearchContext searchContext, final Class<?> resolverClass, List<Class<?>> interfaceClasses) {
        searchContext.searchHierarchyLevel++;
        for(Class<?> interfaceClass : interfaceClasses) {
            List<Class<?>> superInterfaces = getSuperInterfaces(interfaceClass);
            if(!superInterfaces.isEmpty()) {
                findDifferenceForSuperInterfaces(searchContext, resolverClass, superInterfaces);
            }
            if (interfaceClass.getCanonicalName().equals(resolverClass.getCanonicalName())) {
                addToFoundWhenIsClosetsMatch(searchContext, resolverClass, searchContext.searchHierarchyLevel);
            }
        }
        searchContext.searchHierarchyLevel--;
    }

    private List<Class<?>> getSuperInterfaces(Class<?> type) {
        return new ArrayList<>(Arrays.asList(type.getInterfaces()));
    }


    @Data
    private static class SearchContext {
        final List<Class<?>> foundClasses = new ArrayList<>();
        int currentNearCounter = Integer.MAX_VALUE;
        int searchHierarchyLevel = 0;

        public void add(Class<?> type) {
            if (!foundClasses.contains(type)) {
                foundClasses.add(type);
            }

        }

        public void clear() {
            foundClasses.clear();
        }
    }
}
