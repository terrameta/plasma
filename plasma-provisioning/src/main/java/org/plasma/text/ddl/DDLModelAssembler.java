/**
 * Copyright 2017 TerraMeta Software, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.plasma.text.ddl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.plasma.runtime.DataStoreType;
import org.plasma.runtime.PlasmaRuntime;
import org.plasma.sdo.DataType;
import org.plasma.sdo.PlasmaProperty;
import org.plasma.sdo.PlasmaType;
import org.plasma.sdo.helper.PlasmaTypeHelper;
import org.plasma.sdo.profile.KeyType;
import org.plasma.sdo.repository.Class_;
import org.plasma.sdo.repository.Enumeration;
import org.plasma.sdo.repository.EnumerationLiteral;
import org.plasma.sdo.repository.Namespace;
import org.plasma.sdo.repository.OpaqueBehavior;
import org.plasma.sdo.repository.PlasmaRepository;

import commonj.sdo.Property;
import commonj.sdo.Type;

public class DDLModelAssembler {
  private static Log log = LogFactory.getLog(DDLModelAssembler.class);

  private Schemas schemas;
  // maps namespace physical names to maps of type physical names to types
  private Map<String, Map<String, PlasmaType>> schemaMap = new HashMap<String, Map<String, PlasmaType>>();

  // type physical names to namespace physical names
  private Map<String, String> reverseSchemaMap = new HashMap<String, String>();

  public DDLModelAssembler() {
    this(PlasmaRepository.getInstance().getAllNamespaces());
  }

  public DDLModelAssembler(String[] namespaces) {
    this(resultList(namespaces));
  }

  private static List<Namespace> resultList(String[] namespaces) {
    List<Namespace> result = new ArrayList<Namespace>();
    Map<String, String> map = new HashMap<String, String>();
    for (String uri : namespaces)
      map.put(uri, uri);
    for (Namespace namespace : PlasmaRepository.getInstance().getAllNamespaces()) {
      if (map.containsKey(namespace.getUri()))
        result.add(namespace);
    }
    return result;
  }

  public DDLModelAssembler(List<Namespace> namespaces) {
    if (log.isDebugEnabled())
      log.debug("loading " + namespaces.size() + " namespaces");

    schemas = new Schemas();
    // load maps
    for (Namespace namespace : namespaces) {
      log.debug("processing namespace: " + namespace.getUri());
      if (!PlasmaRuntime.getInstance().hasNamespace(DataStoreType.RDBMS)) {
        log.debug("ignoring non " + DataStoreType.RDBMS.name() + " namespace: "
            + namespace.getUri());
        continue;
      }

      List<Type> types = PlasmaTypeHelper.INSTANCE.getTypes(namespace.getUri());
      for (Type type : types) {
        PlasmaType plasmaType = (PlasmaType) type;
        String typePhysicalName = null;
        if (plasmaType.getPhysicalName() == null || plasmaType.getPhysicalName().length() == 0) {
          typePhysicalName = this.derivePhysicalName(plasmaType);
        } else
          typePhysicalName = plasmaType.getPhysicalName();
        String namespacePhysicalName = null;
        if (namespace.getPhysicalName() == null || namespace.getPhysicalName().trim().length() == 0) {
          namespacePhysicalName = this.derivePhysicalName(namespace);
        } else
          namespacePhysicalName = namespace.getPhysicalName();
        Map<String, PlasmaType> typeMap = schemaMap.get(namespacePhysicalName);
        if (typeMap == null) {
          typeMap = new HashMap<String, PlasmaType>();
          schemaMap.put(namespacePhysicalName, typeMap);
        }
        typeMap.put(typePhysicalName, plasmaType);
        reverseSchemaMap.put(typePhysicalName, namespacePhysicalName);
      }
    }

    // create
    for (String schemaName : schemaMap.keySet()) {
      log.debug("creating schema: " + schemaName);
      Schema schema = createSchema(schemaName);
      Map<String, PlasmaType> typeMap = schemaMap.get(schemaName);
      for (PlasmaType type : typeMap.values()) {
        if (type.isAbstract()) {
          if (log.isDebugEnabled())
            log.debug("skipping abstract type, " + type);
          continue;
        }

        Table table = createTable(schema, type);
        schema.getTables().add(table);

        List<Property> properties = type.getProperties(); // returns all base
                                                          // type(s) props as
                                                          // well
        Map<Property, Property> map = new HashMap<Property, Property>();
        for (Property p : properties)
          map.put(p, p);

        createColumns(schema, table, type, map.values());
        createPriKey(table, map.values());

        Class_ repositoryClass = (Class_) type.getClassifier();
        List<OpaqueBehavior> ddlBehaviors = repositoryClass.getOpaqueBehaviors("DDL");
        createBehaviors(table, ddlBehaviors);
        List<OpaqueBehavior> sqlBehaviors = repositoryClass.getOpaqueBehaviors("SQL");
        createBehaviors(table, sqlBehaviors);

        createForeignConstraints(table, type, map.values());
        createUniqueConstraints(table, map.values());
        createCheckConstraints(table, map.values());
        createIndexes(table, map.values());
      }
    }
  }

  private void createIndexes(Table table, Collection<Property> properties) {
    int i = 1;
    for (Property prop : properties) {
      PlasmaProperty plasmaProperty = (PlasmaProperty) prop;
      if (plasmaProperty.getPhysicalName() == null)
        continue;
      if (plasmaProperty.getType().isDataType())
        continue;
      if (plasmaProperty.isMany())
        continue;

      Index index = new Index();
      index.setName("I_" + table.getName() + String.valueOf(i));
      index.setColumn(plasmaProperty.getPhysicalName());
      table.getIndices().add(index);
      i++;
    }
  }

  private void createCheckConstraints(Table table, Collection<Property> properties) {
    int i = 1;
    for (Property prop : properties) {
      PlasmaProperty plasmaProperty = (PlasmaProperty) prop;
      if (plasmaProperty.getPhysicalName() == null)
        continue;

      if (!plasmaProperty.getType().isDataType())
        continue;
      if (plasmaProperty.getRestriction() == null)
        continue;

      Check check = new Check();
      check.setName("CK_" + table.getName() + "_" + String.valueOf(i));
      check.setColumn(plasmaProperty.getPhysicalName());
      table.getChecks().add(check);
      Enumeration restriction = plasmaProperty.getRestriction();
      for (EnumerationLiteral lit : restriction.getOwnedLiteral()) {
        check.getValues().add(lit.getPhysicalName());
      }
      i++;
    }
  }

  private void createUniqueConstraints(Table table, Collection<Property> properties) {
    int uniqueCount = 0;
    for (Property prop : properties) {
      PlasmaProperty plasmaProperty = (PlasmaProperty) prop;
      if (plasmaProperty.getPhysicalName() == null)
        continue;
      if (plasmaProperty.getKey() != null
          && plasmaProperty.getKey().getType().ordinal() == KeyType.primary.ordinal()) {
        continue; // already unique
      }

      Boolean isUnique = (Boolean) plasmaProperty
          .get(PlasmaProperty.INSTANCE_PROPERTY_BOOLEAN_ISUNIQUE);
      if (isUnique != null && isUnique.booleanValue())
        uniqueCount++;
    }

    if (uniqueCount > 0) {
      Unique unique = new Unique();
      unique.setName("UK_" + table.getName());
      table.getUniques().add(unique);

      int i = 1;
      for (Property prop : properties) {
        PlasmaProperty plasmaProperty = (PlasmaProperty) prop;
        if (plasmaProperty.getPhysicalName() == null)
          continue;
        if (plasmaProperty.getKey() != null
            && plasmaProperty.getKey().getType().ordinal() == KeyType.primary.ordinal()) {
          continue; // already unique
        }
        Boolean isUnique = (Boolean) plasmaProperty
            .get(PlasmaProperty.INSTANCE_PROPERTY_BOOLEAN_ISUNIQUE);
        if (isUnique == null || !isUnique.booleanValue())
          continue;
        On on = new On();
        on.setColumn(plasmaProperty.getPhysicalName());
        unique.getOns().add(on);
        i++;
      }
    }
  }

  private void createForeignConstraints(Table table, PlasmaType plasmaType,
      Collection<Property> properties) {
    int i = 1;
    for (Property prop : properties) {
      PlasmaProperty plasmaProperty = (PlasmaProperty) prop;
      if (plasmaProperty.getType().isDataType())
        continue; // only ref props
      if (plasmaProperty.isMany())
        continue; // only singular props

      String physicalName = null;
      if (plasmaProperty.getPhysicalName() == null) {
        log.warn("no physical name found for singular property, "
            + plasmaProperty.getContainingType().getURI() + "#"
            + plasmaProperty.getContainingType().getName() + "." + plasmaProperty.getName()
            + " - deriving");
        physicalName = this.derivePhysicalName(plasmaProperty);
      } else
        physicalName = plasmaProperty.getPhysicalName();

      Fk fk = new Fk();
      fk.setName("FK_" + table.getName() + String.valueOf(i));
      fk.setColumn(physicalName);

      Type oppositeType = plasmaProperty.getType();
      if (!oppositeType.isAbstract()) {
        String typePhysicalName = null;
        if (((PlasmaType) oppositeType).getPhysicalName() != null)
          typePhysicalName = ((PlasmaType) oppositeType).getPhysicalName();
        else
          typePhysicalName = this.derivePhysicalName((PlasmaType) oppositeType);
        String schemaPhysicalname = this.reverseSchemaMap.get(typePhysicalName);
        fk.setToTable(typePhysicalName);
        fk.setToSchema(schemaPhysicalname);
      } else // FIXME: collapse all references in abstract classes into subclass
      {
        String typePhysicalName = null;
        if (plasmaType.getPhysicalName() != null)
          typePhysicalName = plasmaType.getPhysicalName();
        else
          typePhysicalName = this.derivePhysicalName(plasmaType);
        String schemaPhysicalname = this.reverseSchemaMap.get(typePhysicalName);
        fk.setToTable(typePhysicalName);
        fk.setToSchema(schemaPhysicalname);
      }

      table.getFks().add(fk);
      i++;
    }
  }

  private void createBehaviors(Table table, List<OpaqueBehavior> behaviors) {
    for (OpaqueBehavior behavior : behaviors) {
      BehaviorType type = null;
      try {
        type = BehaviorType.fromValue(behavior.getName().toLowerCase());
      } catch (IllegalArgumentException e) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < BehaviorType.values().length; i++) {
          if (i > 0)
            buf.append(", ");
          buf.append(BehaviorType.values()[i].value());
        }
        throw new DDLException("unknown behavior name '" + behavior.getName()
            + "' - expected one of [" + buf.toString() + "]");
      }
      Behavior ddlBehavior = new Behavior();
      ddlBehavior.setType(type);
      ddlBehavior.setValue(behavior.getBody());
      if (!ddlBehavior.getValue().trim().endsWith(":")) {
        if (log.isDebugEnabled())
          log.debug("appending DDL statement terminator for '" + ddlBehavior.getType() + "'");
        ddlBehavior.setValue(ddlBehavior.getValue().trim() + ";");
      }
      table.getBehaviors().add(ddlBehavior);
    }
  }

  private String derivePhysicalName(PlasmaProperty plasmaProperty) {
    String derivedPhysicalName = null;
    if (plasmaProperty.getType().isDataType()) {
      derivedPhysicalName = plasmaProperty.getName().toUpperCase();
    } else {
      Type oppositeType = plasmaProperty.getType();
      derivedPhysicalName = ((PlasmaType) oppositeType).getPhysicalName();
      if (derivedPhysicalName == null)
        derivedPhysicalName = derivePhysicalName((PlasmaType) oppositeType);
    }
    if (derivedPhysicalName == null || derivedPhysicalName.trim().length() == 0)
      throw new DDLException("could not derive physical name for property, " + plasmaProperty);
    return derivedPhysicalName;
  }

  private String derivePhysicalName(PlasmaType plasmatype) {
    String derivedPhysicalName = plasmatype.getName().toUpperCase();
    return derivedPhysicalName;
  }

  private String derivePhysicalName(Namespace namespace) {
    String derivedPhysicalName = namespace.getName().toUpperCase();
    return derivedPhysicalName;
  }

  private void createColumns(Schema schema, Table table, PlasmaType plasmaType,
      Collection<Property> properties) {
    for (Property prop : properties) {
      PlasmaProperty plasmaProperty = (PlasmaProperty) prop;
      if (plasmaProperty.isMany())
        continue;
      String derivedPhysicalName = null;
      if (plasmaProperty.getPhysicalName() == null) {
        log.warn("no physical name found for singular property, "
            + plasmaProperty.getContainingType().getURI() + "#"
            + plasmaProperty.getContainingType().getName() + "." + plasmaProperty.getName()
            + " - deriving");
        derivedPhysicalName = derivePhysicalName(plasmaProperty);
      }
      Column column = null;
      if (derivedPhysicalName == null) {
        column = createColumn(schema, table, plasmaType, plasmaProperty);
      } else {
        column = createColumn(schema, table, plasmaType, plasmaProperty, derivedPhysicalName);
      }
      table.getColumns().add(column);
    }
  }

  private void createPriKey(Table table, Collection<Property> properties) {
    for (Property prop : properties) {
      PlasmaProperty plasmaProperty = (PlasmaProperty) prop;
      if (plasmaProperty.getPhysicalName() == null)
        continue;
      if (!plasmaProperty.isKey(KeyType.primary))
        continue;

      Pk pk = table.getPk();
      if (pk == null) {
        pk = createPk(table, plasmaProperty);
        table.setPk(pk);
      }
      On on = new On();
      on.setColumn(plasmaProperty.getPhysicalName());
      table.getPk().getOns().add(on);
    }
  }

  private Pk createPk(Table table, PlasmaProperty plasmaProperty) {
    Pk pk = new Pk();
    pk.setName("PK_" + table.getName());
    return pk;
  }

  private Column createColumn(Schema schema, Table table, PlasmaType plasmaType,
      PlasmaProperty plasmaProperty) {
    return this.createColumn(schema, table, plasmaType, plasmaProperty, null);
  }

  private Column createColumn(Schema schema, Table table, PlasmaType plasmaType,
      PlasmaProperty plasmaProperty, String derivedPhysicalName) {
    Column column = new Column();
    if (derivedPhysicalName != null)
      column.setName(derivedPhysicalName);
    else
      column.setName(plasmaProperty.getPhysicalName());
    column.setNullable(plasmaProperty.isNullable());
    if (plasmaProperty.getMaxLength() > 0)
      column.setSize(plasmaProperty.getMaxLength());
    else
      column.setSize(-1);
    if (plasmaProperty.getType().isDataType()) {
      DataType sdoType = DataType.valueOf(plasmaProperty.getType().getName());
      column.setType(sdoType.name());
    } else {

      PlasmaProperty oppositePkProp = null;

      // FIXME: assumes a single PK !!
      if (!plasmaProperty.getType().isAbstract()) {
        for (Property p : plasmaProperty.getType().getProperties()) {
          PlasmaProperty oppositeProp = (PlasmaProperty) p;
          if (oppositeProp.isKey(KeyType.primary)) {
            if (oppositePkProp != null)
              throw new DDLException("multiple opposite pri-key propertys found for '"
                  + plasmaProperty.getContainingType().getURI() + "#"
                  + plasmaProperty.getContainingType().getName() + "." + plasmaProperty.getName()
                  + "'");
            oppositePkProp = (PlasmaProperty) oppositeProp;
          }
        }
      } else {
        oppositePkProp = (PlasmaProperty) plasmaType.findProperty(KeyType.primary);

      }

      if (oppositePkProp == null)
        throw new DDLException("could not find opposite pri-key property for '"
            + plasmaProperty.getContainingType().getURI() + "#"
            + plasmaProperty.getContainingType().getName() + "." + plasmaProperty.getName() + "'");
      DataType sdoType = DataType.valueOf(oppositePkProp.getType().getName());
      column.setType(sdoType.name());
    }

    return column;
  }

  private Table createTable(Schema schema, PlasmaType plasmaType) {
    Table table = new Table();
    String typePhysicalName = null;
    if (plasmaType.getPhysicalName() != null)
      typePhysicalName = plasmaType.getPhysicalName();
    else
      typePhysicalName = this.derivePhysicalName(plasmaType);
    log.debug("creating table: " + typePhysicalName);
    table.setName(typePhysicalName);
    return table;
  }

  private Schema createSchema(String name) {
    Schema schema = new Schema();
    this.schemas.getSchemas().add(schema);
    schema.setName(name);
    return schema;
  }

  public Schemas getSchemas() {
    return this.schemas;
  }

  /*
   * private JDBCType mapType(DataType dataType) { switch (dataType) { case
   * Boolean: return JDBCType.BOOLEAN; case Byte: return JDBCType.TINYINT; case
   * Bytes: return JDBCType.VARBINARY; case Character: return JDBCType.CHAR;
   * case Decimal: return JDBCType.DECIMAL; case Double: return JDBCType.DOUBLE;
   * case Float: return JDBCType.FLOAT; case Short: return JDBCType.SMALLINT;
   * case Int: return JDBCType.INTEGER; case Integer: case Long: return
   * JDBCType.BIGINT; case String: case Strings: case URI: return
   * JDBCType.VARCHAR; case Object: return JDBCType.VARBINARY; case Date: return
   * JDBCType.DATE; case DateTime: return JDBCType.TIMESTAMP; case Time: return
   * JDBCType.TIME; case Day: case Duration: case Month: case MonthDay: case
   * Year: case YearMonth: case YearMonthDay: default: throw new
   * DDLException("unsupported SDO type, " + dataType.toString()); } }
   */
}
