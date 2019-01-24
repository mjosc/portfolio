package core;

import org.gedcom4j.model.Individual;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;



public class GedcomPerson {

  private List<Long> childFamilies;
  private List<Long> parentFamilies;

  public GedcomPerson(Individual ind) {
//    super(GedcomImporter.xrefToId(ind.getXref()));

//    facts.add(new Fact("name", ind.getFormattedName()));

//    importer.suppliers.forEach(f -> f.apply(null, null));

  }
//
//  private AbstractAttributeImporter importer = new AbstractAttributeImporter() {
//
//    @Override
//    public BiFunction name() {
//      return (x, y) -> new Fact("name", "matt");
//    }
//
//    @Override
//    public BiFunction random() {
//      return null;
//    }
//
//  };
//
//
//
//  @Override
//  public long getId() {
//    return id;
//  }
//
//  @Override
//  public List<Fact> getFacts() {
//    return facts;
//  }
}
