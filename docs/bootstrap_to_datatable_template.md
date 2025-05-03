# BootstrapTable to DataTable Migration Guide

This guide provides instructions for migrating from BootstrapTable to DataTable components as part of the React 18 upgrade.

## Step 1: Rename the file extension

Rename the file from `.js` to `.jsx`.

## Step 2: Update imports

Remove these imports:
```javascript
import ToolkitProvider, {Search} from "react-bootstrap-table2-toolkit";
import BootstrapTable from "react-bootstrap-table-next";
import paginationFactory from "react-bootstrap-table2-paginator";
```

Add this import:
```javascript
import DataTable from "../../../common/DataTable.jsx"; // Adjust the path as needed
```

Add this at the top of the file to avoid ESLint warnings:
```javascript
/* global console */
```

## Step 3: Update column definitions

Change the column definitions from BootstrapTable format to Tanstack Table format:

### Before:
```javascript
const columns = [
  {
    dataField: "name",
    text: "Name",
    sort: true,
    formatter: (c, d) => d.name,
    sortFunc: (a, b, order, dataField, rowA, rowB) => {
      // Custom sort function
    }
  },
  // Other columns...
];
```

### After:
```javascript
const columns = [
  {
    accessorKey: "name",
    header: "Name",
    enableSorting: true,
    cell: ({ row }) => row.original.name,
  },
  // Other columns...
];
```

Key changes:
- `dataField` → `accessorKey`
- `text` → `header`
- `sort` → `enableSorting`
- `formatter` → `cell`
- In the `cell` function, use `row.original` to access the row data

## Step 4: Replace BootstrapTable with DataTable

Replace the ToolkitProvider and BootstrapTable components:

### Before:
```javascript
<ToolkitProvider
  keyField="id"
  data={data}
  columns={columns}
  search
>
  {props => (
    <div>
      <div className="float-end">
        <Search.SearchBar {...props.searchProps} />
      </div>
      <BootstrapTable
        bootstrap4
        keyField="id"
        bordered={false}
        pagination={paginationFactory({
          sizePerPage: 10,
          sizePerPageList: [10, 20, 40, 80]
        })}
        defaultSorted={[{
          dataField: "name",
          order: "asc"
        }]}
        {...props.baseProps}
      >
      </BootstrapTable>
    </div>
  )}
</ToolkitProvider>
```

### After:
```javascript
<DataTable
  data={data}
  columns={columns}
  defaultSort={{ id: "name", desc: false }}
  defaultPageSize={10}
/>
```

## Step 5: Update imports in other files

After renaming all React component files from `.js` to `.jsx`, run the `update_imports.sh` script to update import statements in all files.

```bash
cd client
chmod +x update_imports.sh
./update_imports.sh
```

## Step 6: Test the changes

Run the application and verify that the tables are working correctly.