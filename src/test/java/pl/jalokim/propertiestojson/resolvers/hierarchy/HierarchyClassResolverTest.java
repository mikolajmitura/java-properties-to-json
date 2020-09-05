package pl.jalokim.propertiestojson.resolvers.hierarchy;


import static java.util.Arrays.asList;
import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Assertions.assertThat;
import static pl.jalokim.utils.string.StringUtils.concatElementsAsLines;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.junit.Test;
import pl.jalokim.propertiestojson.util.exception.ParsePropertiesException;

public class HierarchyClassResolverTest {

    public static final String ERR_MSG_FORMAT = "Found %s resolvers for instance type: %s%nfound resolvers:%n%s";

    @Test
    public void willFoundTheSameTypeOfResolver() {
        // given
        HierarchyClassResolver hierarchyClassResolver = new HierarchyClassResolver(asList(Object.class, Number.class, Comparable.class, BigDecimal.class));
        BigDecimal bigDecimal = BigDecimal.ONE;
        // when
        Class<?> aClass = hierarchyClassResolver.searchResolverClass(bigDecimal);
        // then
        assertThat(aClass).isEqualTo(BigDecimal.class);
    }

    @Test
    public void foundCollectionInResolverTypeForArrayList() {
        // given
        HierarchyClassResolver hierarchyClassResolver = new HierarchyClassResolver(asList(Collection.class, Double.class));
        List<String> arrayList = Arrays.asList("test1", "test2");
        // when
        Class<?> aClass = hierarchyClassResolver.searchResolverClass(arrayList);
        // then
        assertThat(aClass).isEqualTo(Collection.class);
    }

    @Test
    public void foundToMuchResolversToMuchInterfaces() {
        // given
        HierarchyClassResolver hierarchyClassResolver = new HierarchyClassResolver(
            asList(HasMind.class, HasHair.class, HasLegs.class, Object.class, HasBrain.class, HasAwareness.class));

        Human human = new Human();
        // when
        try {
            hierarchyClassResolver.searchResolverClass(human);
            fail();
        } catch (ParsePropertiesException ex) {
            // then
            assertThat(ex.getMessage()).isEqualTo(String.format(ERR_MSG_FORMAT,
                3,
                Human.class.getCanonicalName(),
                concatElementsAsLines(asList(HasMind.class, HasHair.class, HasLegs.class))
            ));
        }
    }

    @Test
    public void foundAnimalAsResolverTypeNorToMuchInterfaces() {
        // given
        HierarchyClassResolver hierarchyClassResolver = new HierarchyClassResolver(
            asList(Animal.class, HasMind.class, HasHair.class, HasLegs.class, Object.class, HasBrain.class, CanFly.class));
        Human human = new Human();
        // when
        Class<?> aClass = hierarchyClassResolver.searchResolverClass(human);
        // then
        assertThat(aClass).isEqualTo(Animal.class);
    }

    @Test
    public void foundNumberAsSuperClassBecauseIsMoreSufficientThanInterfaceTypes() {
        // given
        HierarchyClassResolver hierarchyClassResolver = new HierarchyClassResolver(asList(Object.class, Number.class, Comparable.class));
        BigDecimal bigDecimal = BigDecimal.ONE;
        // when
        Class<?> aClass = hierarchyClassResolver.searchResolverClass(bigDecimal);
        // then
        assertThat(aClass).isEqualTo(Number.class);
    }

    @Test
    public void foundOnlyObjectAsResolverTypeBecauseNothingMatches() {
        // given
        HierarchyClassResolver hierarchyClassResolver = new HierarchyClassResolver(asList(Object.class, Number.class, Comparable.class));
        Human human = new Human();
        // when
        Class<?> aClass = hierarchyClassResolver.searchResolverClass(human);
        // then
        assertThat(aClass).isEqualTo(Object.class);
    }

    @Test
    public void returnNullWhenNothingMatch() {
        // given
        HierarchyClassResolver hierarchyClassResolver = new HierarchyClassResolver(asList(Number.class, Comparable.class));
        Human human = new Human();
        // when
        Class<?> aClass = hierarchyClassResolver.searchResolverClass(human);
        // then
        assertThat(aClass).isNull();
    }

    @Test
    public void foundInterfaceBecauseIsMoreSufficientThanObject() {
        // given
        HierarchyClassResolver hierarchyClassResolver = new HierarchyClassResolver(asList(Object.class, HasBrain.class, HasMind.class));
        Alien alien = new Alien();
        // when
        Class<?> aClass = hierarchyClassResolver.searchResolverClass(alien);
        // then
        assertThat(aClass).isEqualTo(HasMind.class);
    }

    @Test
    public void foundSuperSuperObjectBecauseIsMoreSufficientThanOthers() {
        // given
        HierarchyClassResolver hierarchyClassResolver = new HierarchyClassResolver(asList(Object.class, Alien.class, HasLegs.class));
        Alien alien = new SuperSuperAlien();
        // when
        Class<?> aClass = hierarchyClassResolver.searchResolverClass(alien);
        // then
        assertThat(aClass).isEqualTo(Alien.class);
    }

    @Test
    public void foundInterfaceBecauseIsMoreSufficientThanOthers() {
        // given
        HierarchyClassResolver hierarchyClassResolver = new HierarchyClassResolver(asList(Object.class, SuperSuperAlienFunc.class, HasLegs.class));
        Alien alien = new SuperSuperAlien();
        // when
        Class<?> aClass = hierarchyClassResolver.searchResolverClass(alien);
        // then
        assertThat(aClass).isEqualTo(SuperSuperAlienFunc.class);
    }

    @Test
    public void foundObjectBecauseIsMoreSufficientThanOthers() {
        // given
        HierarchyClassResolver hierarchyClassResolver = new HierarchyClassResolver(asList(Object.class, Alien.class, HasLegs.class));
        Alien alien = new Super3xAlien();
        // when
        Class<?> aClass = hierarchyClassResolver.searchResolverClass(alien);
        // then
        assertThat(aClass).isEqualTo(Alien.class);
    }

    @Test
    public void foundInterfaceFromSuperClassBecauseIsMoreSufficientThanOthers() {
        // given
        HierarchyClassResolver hierarchyClassResolver = new HierarchyClassResolver(asList(Object.class, SuperSuperAlienFunc.class, HasLegs.class, Alien.class));
        Alien alien = new Super3xAlien();
        // when
        Class<?> aClass = hierarchyClassResolver.searchResolverClass(alien);
        // then
        assertThat(aClass).isEqualTo(SuperSuperAlienFunc.class);
    }

    @Test
    public void foundInterfaceFromSuperClassBecauseIsMoreSufficientThanObject() {
        // given
        HierarchyClassResolver hierarchyClassResolver = new HierarchyClassResolver(asList(Object.class, HasBrain.class, HasMind.class));
        Alien alien = new SuperAlien();
        // when
        Class<?> aClass = hierarchyClassResolver.searchResolverClass(alien);
        // then
        assertThat(aClass).isEqualTo(HasMind.class);
    }

    @Test
    public void foundSuperClassBecauseIsMoreSufficientThanObject() {
        // given
        HierarchyClassResolver hierarchyClassResolver = new HierarchyClassResolver(asList(Object.class, Alien.class, HasLegs.class));
        Alien alien = new SuperAlien();
        // when
        Class<?> aClass = hierarchyClassResolver.searchResolverClass(alien);
        // then
        assertThat(aClass).isEqualTo(Alien.class);
    }

    @Test
    public void foundInterfaceBecauseIsMoreSufficientThanInterfacesOfSuperClass() {
        // given
        HierarchyClassResolver hierarchyClassResolver = new HierarchyClassResolver(asList(Object.class, HasMind.class, HasLegs.class));
        Alien alien = new SuperAlien();
        // when
        Class<?> aClass = hierarchyClassResolver.searchResolverClass(alien);
        // then
        assertThat(aClass).isEqualTo(HasLegs.class);
    }

    @Test
    public void foundTowInterfacesBecauseIsMoreSufficientThanObject() {
        // given
        HierarchyClassResolver hierarchyClassResolver = new HierarchyClassResolver(asList(Object.class, HasBrain.class, HasMind.class, CanFly.class));
        Alien alien = new Alien();
        // when
        try {
            hierarchyClassResolver.searchResolverClass(alien);
            fail();
        } catch (ParsePropertiesException ex) {
            // then
            assertThat(ex.getMessage()).isEqualTo(String.format(ERR_MSG_FORMAT,
                2,
                Alien.class.getCanonicalName(),
                concatElementsAsLines(asList(HasMind.class, CanFly.class))
            ));
        }
    }

    private interface CanStartMoving {

    }


    private interface CanStopMoving {

    }

    private interface HasLegs extends CanStartMoving, CanStopMoving {

    }

    private interface HasBrain extends HasAwareness {

    }

    private interface HasAwareness {

    }

    private interface HasMind extends HasBrain {

    }

    private interface HasHair {

    }

    private interface CanFly {

    }

    private interface SuperSuperAlienFunc {

    }

    private interface Super3xAlienFunc {

    }

    private class Animal {

    }

    private class Alien implements HasMind, CanFly {

    }

    private class Human extends Animal implements HasMind, HasHair, HasLegs, CanStartMoving {

    }

    private class SuperAlien extends Alien implements HasLegs {

    }

    private class SuperSuperAlien extends SuperAlien implements SuperSuperAlienFunc {

    }

    private class Super3xAlien extends SuperSuperAlien implements Super3xAlienFunc {

    }
}