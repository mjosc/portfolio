class FamilyGraph {
  constructor() {
    this.personMap = new Map();
    this.familyMap = new Map();
  }

  addPerson(p) {
    let id = p.id;
    if (this.personMap.has(id)) {
      return false;
    }
    this.personMap.set(id, p);
    return true;
  }

  addFamily(f) {
    let id = f.id;
    if (this.familyMap.has(id)) {
      return false;
    }
    this.familyMap.set(id, f);
    return true;
  }
}

class Person {
  constructor(id, name) {
    this.id = id;
    this.name = name;
    this.childFamily;
    this.parentFamilies;
  }
}

class Family {
  constructor(id) {
    this.id = id;
    this.parents;
    this.children;
  }
}

export {
  FamilyGraph,
  Person,
  Family
}
