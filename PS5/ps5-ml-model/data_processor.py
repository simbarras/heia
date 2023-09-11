#!/usr/bin/env python3
# -*- coding: utf-8 -*-

# Copyright 2023, School of Engineering and Architecture of Fribourg
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.


"""
Create the new dataset.
"""

__author__ = "Simon Barras"
__date__ = "2023-01-31"
__version__ = "0.0.1"
__email__ = "simon.barras@edu.hefr.ch"
__userid__ = "simon.barras"

# IMPORTS
import pandas as pd
import aiohttp
import asyncio
import urllib.parse
import fusion_predictor
import argparse
from rdkit import Chem
from mordred import Calculator, descriptors

# CONSTANTS
NB_COLUMNS = 5  # Number of columns for the cation/anion
OLD_FILE = 'resources/SVM_data.xlsx'
NEW_FILE = 'resources/PS5_data_new.xlsx'


# ------------------------------------#
# Get SMILES
# ------------------------------------#
def read_anion_cation():
    """
    Read the anion/cation from the old dataset
    :return: return the whole structure of the anion/cation
    """
    frame = pd.read_excel(OLD_FILE, sheet_name='IL structure')
    cation_frame = frame.iloc[:, :NB_COLUMNS]
    cation_frame.dropna(subset=['Number'], inplace=True)
    anion_frame = frame.iloc[:, NB_COLUMNS + 1:]
    anion_frame.dropna(subset=['Number.1'], inplace=True)
    maper = {'to test.1': 'to test', 'Number.1': 'Number', 'Abbreviation.1': 'Abbreviation',
             'Cation Full Name': 'Full Name', 'Anion Full Name': 'Full Name', 'Structure': 'SMILES',
             'Structure.1': 'SMILES'}
    cation_frame.rename(columns=maper, inplace=True)
    anion_frame.rename(columns=maper, inplace=True)
    return pd.concat([cation_frame, anion_frame]).reset_index(drop=True)


async def translate(frame):
    """
    Translate the anion/cation name into a SMILES
    :param frame: structure of the anion/cation
    :return: the new structure with the SMILES
    """
    async def get_smiles(s, n):
        url = f'https://cactus.nci.nih.gov/chemical/structure/{urllib.parse.quote(n, safe="")}/smiles'
        print(f'Request: {url}')
        async with s.get(url) as response:
            smiles = await response.text()
            return n, smiles

    result = {}
    async with aiohttp.ClientSession() as session:
        tasks = []
        for name in frame:
            tasks.append(asyncio.ensure_future(get_smiles(session, name)))
            await asyncio.sleep(0, 2)  # Timer to avoid api crash

        names_with_smiles = await asyncio.gather(*tasks)
        for name_with_smile in names_with_smiles:
            smiles_result = name_with_smile[1]
            if smiles_result == '<h1>Page not found (404)</h1>\n':
                smiles_result = ''
            print(f'Result for {name_with_smile[0]}: {smiles_result}')
            result[name_with_smile[0]] = smiles_result

    return result


# ------------------------------------#
# Rewrite the dataset
# ------------------------------------#
def rewrite_dataset(sheet_name, s):
    """
    Rewrite the dataset with the new SMILES and without data
    :param sheet_name:
    :param s: structure of the anion/cation
    :return: the new dataset
    """
    structure = dict(zip(s['Number'], s['SMILES']))
    frame, y = fusion_predictor.read_old_dataset(sheet_name)
    # concat X and y
    frame['exp (K)'] = y

    # Replace the anion/cation number by the smiles of the anion/cation contained in the structure
    new_dataset = {'SMILES': [], 'cation': [], 'anion': [], 'exp (K)': []}
    smiles = [""]
    for i in range(1, len(frame['cation']) + 1):
        cation = frame['cation'][i]
        anion = frame['anion'][i]

        new_dataset['SMILES'].append(f"{structure[cation]}.{structure[anion]}")
        smiles.append(f"{structure[cation]}.{structure[anion]}")
        new_dataset['exp (K)'].append(frame['exp (K)'][i])
        new_dataset['anion'].append(anion)
        new_dataset['cation'].append(cation)

    # Replace cation and anion by their SMILES
    new_dataset = pd.DataFrame(new_dataset)
    smiles = pd.Series(smiles)
    smiles = smiles.drop([0])
    # insert the SMILES in the first column
    frame.insert(0, 'SMILES', smiles, True)
    return new_dataset, frame.drop(columns=['exp (K)'])


# ------------------------------------#
# Compute the new data
# ------------------------------------#
def build_new_data(data):
    """
    Compute the descriptors for the new dataset
    :param data:
    :return: new data
    """
    calc = Calculator(descriptors, ignore_3D=True)

    def transform_dataset(dataset, calculator):
        new_dataset = []
        new_smiles_list = []
        new_cation_list = []
        new_anion_list = []
        # Iter on each line of the dataset
        for index, line in dataset.iterrows():
            try:
                mol = Chem.MolFromSmiles(line['SMILES'])
                new_dataset.append(calculator(mol)[:])
                new_smiles_list.append(line['SMILES'])
                new_cation_list.append(line['cation'])
                new_anion_list.append(line['anion'])
            except:
                print(f"Error at line {line['SMILES']}")
        return new_dataset, new_smiles_list, new_cation_list, new_anion_list

    mols, smiles, cations, anions = transform_dataset(data, calc)
    descriptions = [d.description() for d in calc.descriptors]
    frame = pd.DataFrame(mols, columns=descriptions)
    frame = frame.select_dtypes(include=['float64', 'int64'])
    frame.insert(0, 'SMILES', smiles)
    frame.insert(1, 'cation', cations)
    frame.insert(2, 'anion', anions)
    return frame


# ------------------------------------#
# Create the new document
# ------------------------------------#
def write_data(frame, sheet_name):
    """
    Write into the Excel file
    :param frame: to write
    :param sheet_name: to write
    :return: None
    """
    print(f"Writing {frame.shape} in {sheet_name} of {NEW_FILE}")
    with pd.ExcelWriter(NEW_FILE, engine='openpyxl', mode='a', if_sheet_exists='replace') as writer:
        frame.to_excel(writer, sheet_name=sheet_name, index=False)
    print("Done")


# ------------------------------------#
# Main
# ------------------------------------#
async def main(t, rd, d, a):
    # Translate the name to smiles
    if t:
        print("Translate the name to smiles")
        structure = read_anion_cation()
        name_with_smiles = await translate(structure['Full Name'])
        for index, _ in structure.iterrows():
            structure.at[index, 'SMILES'] = name_with_smiles[structure.at[index, 'Full Name']]
        write_data(structure, 'IL structure')
    else:
        print("Load the structure from the file")
        structure = fusion_predictor.read_structure()

    # Rewrite training set and test set
    if rd:
        print("Rewrite training set and test set")
        train_set, old_data_train = rewrite_dataset('Training', structure)
        test_set, old_data_test = rewrite_dataset('Testing', structure)
        # Concat old data
        old_data = pd.concat([old_data_train, old_data_test])
        # Remove duplicates
        old_data = old_data.drop_duplicates(subset=['cation', 'anion'], keep='first')
        # Write the new data
        write_data(pd.DataFrame(train_set), 'Training set')
        write_data(pd.DataFrame(test_set), 'Testing set')
        write_data(old_data, 'Old data')
    else:
        print("Load the training set and test set from the file")
        train_set = fusion_predictor.read_set('Training')
        test_set = fusion_predictor.read_set('Testing')
        old_data = fusion_predictor.read_old_data()

    # Compute the descriptors
    if d:
        print("Compute the descriptors")
        new_data = build_new_data(old_data)
        write_data(new_data, 'New data')
    else:
        print("Load the new data from the file")
        new_data = fusion_predictor.read_new_data()

    # Analyse the dataset
    if a:
        print("Analyse the dataset")
        print("Structure:")
        print(f"\t{structure.describe()}")
        print(f"\t{structure.shape}")
        print(f"\t{structure.head()}")
        print("Training set:")
        print(f"\t{train_set.describe()}")
        print(f"\t{train_set.shape}")
        print(f"\t{train_set.head()}")
        print("Testing set:")
        print(f"\t{test_set.describe()}")
        print(f"\t{test_set.shape}")
        print(f"\t{test_set.head()}")
        print("Old data:")
        print(f"\t{old_data.describe()}")
        print(f"\t{old_data.shape}")
        print(f"\t{old_data.head()}")
        print("New data:")
        print(f"\t{new_data.describe()}")
        print(f"\t{new_data.shape}")
        print(f"\t{new_data.head()}")


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Create the new dataset.')
    parser.add_argument('-t', '--translate', action='store_true', help='Translate the name to smiles')
    parser.add_argument('-r', '--rewrite', action='store_true', help='Rewrite training set and test set')
    parser.add_argument('-d', '--descriptors', action='store_true', help='Compute the descriptors')
    parser.add_argument('-a', '--analyse', action='store_true', help='Analyse the dataset')

    args = parser.parse_args()

    asyncio.run(main(args.translate, args.rewrite, args.descriptors, args.analyse))
